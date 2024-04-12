/*
 * Copyright 2020 Prathab Murugan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myhome.services.springdatajpa;

import com.myhome.domain.HouseMember;
import com.myhome.domain.HouseMemberDocument;
import com.myhome.repositories.HouseMemberDocumentRepository;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.services.HouseMemberDocumentService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

/**
 * is responsible for handling the creation and management of documents associated
 * with house members in a Java application. It provides methods for finding and
 * deleting documents, as well as creating new documents through the use of MultipartFiles.
 * The service also compresses images before saving them to avoid exceeding file size
 * limits.
 */
@Service
public class HouseMemberDocumentSDJpaService implements HouseMemberDocumentService {

  private final HouseMemberRepository houseMemberRepository;
  private final HouseMemberDocumentRepository houseMemberDocumentRepository;
  @Value("${files.compressionBorderSizeKBytes}")
  private int compressionBorderSizeKBytes;
  @Value("${files.maxSizeKBytes}")
  private int maxFileSizeKBytes;
  @Value("${files.compressedImageQuality}")
  private float compressedImageQuality;

  public HouseMemberDocumentSDJpaService(HouseMemberRepository houseMemberRepository,
      HouseMemberDocumentRepository houseMemberDocumentRepository) {
    this.houseMemberRepository = houseMemberRepository;
    this.houseMemberDocumentRepository = houseMemberDocumentRepository;
  }

  /**
   * retrieves a `HouseMemberDocument` object associated with a given `memberId`. It
   * first queries the `houseMemberRepository` for the `HouseMember` object using the
   * `memberId`, and then maps the `HouseMember` object to its corresponding `House
   * Member Document`. The function returns an optional `HouseMemberDocument` object
   * containing the mapped data.
   * 
   * @param memberId unique identifier of the member for which the corresponding House
   * Member Document is being searched.
   * 
   * @returns an optional `HouseMemberDocument`.
   * 
   * The returned Optional object represents an optional HouseMemberDocument. If a
   * HouseMemberDocument is found, the Optional will be Some(HouseMemberDocument),
   * otherwise it will be None. The HouseMemberDocument contained in the Optional has
   * a getHouseMemberDocument() method that returns the HouseMember document itself.
   */
  @Override
  public Optional<HouseMemberDocument> findHouseMemberDocument(String memberId) {
    return houseMemberRepository.findByMemberId(memberId)
        .map(HouseMember::getHouseMemberDocument);
  }

  /**
   * deletes a House Member Document associated with a given member ID by finding the
   * document, setting it to null, and then saving the updated member entity back to
   * the repository. If the document is successfully deleted, the function returns `true`.
   * 
   * @param memberId ID of the member whose House Member Document is to be deleted.
   * 
   * @returns a boolean value indicating whether the house member document was successfully
   * deleted.
   */
  @Override
  public boolean deleteHouseMemberDocument(String memberId) {
    return houseMemberRepository.findByMemberId(memberId).map(member -> {
      if (member.getHouseMemberDocument() != null) {
        member.setHouseMemberDocument(null);
        houseMemberRepository.save(member);
        return true;
      }
      return false;
    }).orElse(false);
  }

  /**
   * updates a house member's document by finding the member in the repository, creating
   * a new document if necessary, and adding it to the member's record.
   * 
   * @param multipartFile file to be updated for the associated member.
   * 
   * 	- `multipartFile`: A `MultipartFile` object representing the uploaded document.
   * It has various attributes such as `filename`, `contentType`, `body`, and `originalFilename`.
   * 
   * @param memberId id of the member whose House Member Document is being updated.
   * 
   * @returns an Optional object containing a House Member Document, created or updated
   * based on the provided Multipart File and member ID.
   * 
   * 	- `Optional<HouseMemberDocument>` is the type of the output returned by the
   * function. This means that the function may or may not return a `HouseMemberDocument`,
   * depending on whether a document was found and created successfully.
   * 	- `houseMemberRepository.findByMemberId(memberId)` returns an `Optional` containing
   * a `HouseMember` object if a member with the given `memberId` exists in the repository,
   * or `empty()` otherwise. This step is necessary to check whether there is a member
   * with the given `memberId` before attempting to create a document for that member.
   * 	- `map(member -> { ... })` is used to transform the `Optional<HouseMember>`
   * returned by `houseMemberRepository.findBy MemberId()` into an `Optional<HouseMemberDocument>`.
   * This involves calling the `tryCreateDocument` method on the `HouseMember` object
   * and storing the resulting `Optional<HouseMemberDocument>` in a variable named `houseMemberDocument`.
   * 	- `houseMemberDocument.ifPresent(document -> addDocumentToHouseMember(document,
   * member))` is used to add the created document to the House Member object if the
   * `houseMemberDocument` Optional is not empty. This involves calling the
   * `addDocumentToHouseMember` method on the `HouseMember` object and passing in the
   * `document` and `member` parameters.
   * 	- `orElse(Optional.empty())` is used to return an `Optional` containing the result
   * of the `tryCreateDocument` method if the `house MemberDocument` Optional is empty.
   * This ensures that the function always returns a non-empty `Optional`, even if there
   * was no document found or created.
   * 
   * In summary, the `updateHouseMemberDocument` function takes a `MultipartFile` and
   * a `String memberId` as input, and returns an `Optional<HouseMemberDocument>`
   * representing the created document, or `empty()` if no document was found or created.
   */
  @Override
  public Optional<HouseMemberDocument> updateHouseMemberDocument(MultipartFile multipartFile,
      String memberId) {
    return houseMemberRepository.findByMemberId(memberId).map(member -> {
      Optional<HouseMemberDocument> houseMemberDocument = tryCreateDocument(multipartFile, member);
      houseMemberDocument.ifPresent(document -> addDocumentToHouseMember(document, member));
      return houseMemberDocument;
    }).orElse(Optional.empty());
  }

  /**
   * takes a `MultipartFile` and a `String` member ID as input, and returns an optional
   * `HouseMemberDocument`. It first retrieves the member from the repository based on
   * the ID, then creates a new document if not already present, adds it to the member,
   * and returns the resulting document.
   * 
   * @param multipartFile file containing the HouseMember document that needs to be
   * created or updated.
   * 
   * 	- `multipartFile`: A deserialized `MultipartFile` object representing a file
   * upload. Its properties may include:
   * 	+ `file`: The original file uploaded by the user.
   * 	+ `originalFilename`: The original filename of the file as provided by the user.
   * 	+ `filename`: The filename of the file after processing, if applicable.
   * 	+ `contentType`: The content type of the file, such as "image/jpeg".
   * 	+ `size`: The size of the file in bytes.
   * 	+ `error`: Any error messages related to the file upload, if applicable.
   * 
   * @param memberId ID of the member for whom the HouseMemberDocument is to be created.
   * 
   * @returns an `Optional` of a `HouseMemberDocument`.
   * 
   * 	- `Optional<HouseMemberDocument>`: This represents an optional instance of
   * `HouseMemberDocument`, which means that the function may or may not return a valid
   * document depending on the input.
   * 	- `houseMemberRepository.findBy MemberId(memberId)`: This is a call to the `findBy
   * MemberId` method of the `houseMemberRepository`, which returns an `Optional<HouseMember>`
   * object representing a house member with the given `memberId`.
   * 	- `map(member -> { ... })`: This is a call to the `map` method, which takes a
   * function as an argument that transforms the `Optional<HouseMember>` object into
   * an `Optional<HouseMemberDocument>`. The function is applied to the `house Member`
   * object and returns an `Optional<HouseMemberDocument>` object if the transformation
   * was successful.
   * 	- `ifPresent(document -> addDocumentToHouseMember(document, member))` : This line
   * adds an optional `HouseMemberDocument` to a `HouseMember` object if the
   * `Optional<HouseMemberDocument>` output from the previous step is present. The
   * `addDocumentToHouseMember` method takes the `HouseMemberDocument` and `House Member`
   * objects as arguments and performs the necessary operations to add the document to
   * the member's profile.
   * 	- `orElse(Optional.empty())`: This line returns an `Optional<HouseMemberDocument>`
   * object that is either empty or contains a valid `HouseMemberDocument` if the
   * previous steps were successful in finding a house member with the given `memberId`.
   */
  @Override
  public Optional<HouseMemberDocument> createHouseMemberDocument(MultipartFile multipartFile,
      String memberId) {
    return houseMemberRepository.findByMemberId(memberId).map(member -> {
      Optional<HouseMemberDocument> houseMemberDocument = tryCreateDocument(multipartFile, member);
      houseMemberDocument.ifPresent(document -> addDocumentToHouseMember(document, member));
      return houseMemberDocument;
    }).orElse(Optional.empty());
  }

  /**
   * takes a multipart file and a member object as input, creates an image from the
   * file, compresses it if necessary, saves it to a file, and returns an optional
   * document object if successful or an empty optional otherwise.
   * 
   * @param multipartFile file to be processed, which contains an image of a member's
   * document.
   * 
   * 	- `multipartFile.getSize()`: The size of the multipart file in bytes.
   * 	- `DataSize.ofKilobytes(compressionBorderSizeKBytes)`: A constant representing a
   * kilobyte-sized buffer for compression.
   * 	- `DataSize.ofKilobytes(maxFileSizeKBytes)`: A constant representing a kilobyte-sized
   * buffer for saving the document to file.
   * 
   * @param member HouseMember for which an image document is being created.
   * 
   * 	- `member`: A HouseMember object representing the member whose document is being
   * created.
   * 	- `multipartFile`: A MultipartFile object containing the image file to be processed.
   * 	- `compressionBorderSizeKBytes`: The size threshold for compressing an image,
   * represented in bytes.
   * 	- `maxFileSizeKBytes`: The maximum file size allowed for a HouseMember document,
   * represented in bytes.
   * 
   * @returns an optional `HouseMemberDocument`, which represents a document containing
   * a member's image.
   * 
   * 	- The `Optional` object contains a `HouseMemberDocument` object if the image was
   * successfully compressed and saved to file, or it is empty if an error occurred
   * during compression or saving.
   * 	- The `HouseMemberDocument` object has a `memberId` field that represents the ID
   * of the member whose document was created, and a `documentPath` field that contains
   * the path to the saved document file.
   * 	- The `documentPath` field is a string that includes the format string
   * `"member_%s_document.jpg"` where `%s` is the value of the `memberId` field.
   */
  private Optional<HouseMemberDocument> tryCreateDocument(MultipartFile multipartFile,
      HouseMember member) {

    try (ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream()) {
      BufferedImage documentImage = getImageFromMultipartFile(multipartFile);
      if (multipartFile.getSize() < DataSize.ofKilobytes(compressionBorderSizeKBytes).toBytes()) {
        writeImageToByteStream(documentImage, imageByteStream);
      } else {
        compressImageToByteStream(documentImage, imageByteStream);
      }
      if (imageByteStream.size() < DataSize.ofKilobytes(maxFileSizeKBytes).toBytes()) {
        HouseMemberDocument houseMemberDocument = saveHouseMemberDocument(imageByteStream,
            String.format("member_%s_document.jpg", member.getMemberId()));
        return Optional.of(houseMemberDocument);
      } else {
        return Optional.empty();
      }
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  /**
   * updates a `HouseMember` instance's `HouseMemberDocument` field and saves it to the
   * repository, creating or updating the associated document in the database.
   * 
   * @param houseMemberDocument HouseMemberDocument object that contains information
   * about the member's documents, which is being added to the member's record in the
   * database through the `save()` method of the `houseMemberRepository`.
   * 
   * 	- `HouseMemberDocument`: This is a class that represents a document related to a
   * house member.
   * 	- `member`: This is an instance of the `HouseMember` class, which represents a
   * member of a house.
   * 	- `house MemberRepository`: This is an interface or class that provides methods
   * for saving and retrieving house members from a repository.
   * 
   * @param member HouseMember that will have its `HouseMemberDocument` associated with
   * it set to the provided `houseMemberDocument`.
   * 
   * 	- `setHouseMemberDocument(houseMemberDocument)` sets the `HouseMemberDocument`
   * field of the `member` object to the provided `houseMemberDocument`.
   * 	- `save()` saves the updated `member` object in the repository.
   * 
   * @returns a saved HouseMember object containing the updated document information.
   * 
   * 	- The `houseMemberDocument` parameter is a `HouseMemberDocument` object that
   * represents the document related to the specified `HouseMember`.
   * 	- The `member` parameter is a `HouseMember` object that represents the member for
   * whom the document is being added.
   * 	- The `houseMemberRepository` is a `HouseMemberRepository` interface that provides
   * methods for saving and retrieving `HouseMember` objects. The `save()` method is
   * used to save the updated `HouseMember` object in the database.
   */
  private HouseMember addDocumentToHouseMember(HouseMemberDocument houseMemberDocument,
      HouseMember member) {
    member.setHouseMemberDocument(houseMemberDocument);
    return houseMemberRepository.save(member);
  }

  /**
   * saves a HouseMemberDocument to the repository, given an image byte stream and a filename.
   * 
   * @param imageByteStream image data of the HouseMemberDocument that needs to be saved.
   * 
   * 	- ` ByteArrayOutputStream imageByteStream`: This is an output stream that contains
   * the serialized image data in a byte array. The size of the byte array can be
   * determined by calling the `toByteArray()` method on the output stream, which returns
   * a raw byte array representation of the image data.
   * 
   * @param filename name of the file that contains the image data to be saved.
   * 
   * @returns a saved HouseMemberDocument entity in the repository.
   * 
   * 	- `HouseMemberDocument`: This is the type of object that is being saved in the
   * `houseMemberDocumentRepository`. It has a filename and an image byte array.
   * 	- `newDocument`: This is the newly created House Member Document object that is
   * being saved. It has a filename and an image byte array.
   * 	- `houseMemberDocumentRepository`: This is the repository where the document is
   * being saved. It is responsible for storing the document in the database.
   */
  private HouseMemberDocument saveHouseMemberDocument(ByteArrayOutputStream imageByteStream,
      String filename) {
    HouseMemberDocument newDocument =
        new HouseMemberDocument(filename, imageByteStream.toByteArray());
    return houseMemberDocumentRepository.save(newDocument);
  }

  /**
   * writes a `BufferedImage` object to a `ByteArrayOutputStream` by calling the
   * `ImageIO.write()` method with the image format as `"jpg"` and the output stream
   * as the specified `ByteArrayOutputStream`.
   * 
   * @param documentImage 2D image that is to be written to a byte stream as a JPEG file.
   * 
   * 	- The `BufferedImage` object `documentImage` contains an image representation of
   * data.
   * 	- The image is serialized to a ` ByteArrayOutputStream` object `imageByteStream`.
   * 	- The `ImageIO` class writes the image representation from `documentImage` to a
   * JPEG file in `imageByteStream`.
   * 
   * @param imageByteStream byte array that will store the image data after it has been
   * written by the `ImageIO.write()` method.
   * 
   * 	- `imageByteStream` is an instance of `ByteArrayOutputStream`, which means it can
   * be used to create a byte array containing the serialized image data.
   * 	- The method `write(Image image, String format, OutputStream outputStream)` is
   * called with the input `documentImage` as the Image object and `"jpg"` as the format
   * string. This writes the image data to the `imageByteStream` instance in JPEG format.
   */
  private void writeImageToByteStream(BufferedImage documentImage,
      ByteArrayOutputStream imageByteStream)
      throws IOException {
    ImageIO.write(documentImage, "jpg", imageByteStream);
  }

  /**
   * compresses a `BufferedImage` using an `ImageWriter` and writes it to a byte stream,
   * allowing for flexible control over compression mode and quality.
   * 
   * @param bufferedImage 2D graphics image to be compressed and written to an output
   * stream.
   * 
   * The `BufferedImage` object represents an image that has been loaded into memory
   * using the `BufferedImage` class. The `BufferedImage` object has various attributes
   * such as height, width, and depth, which correspond to the dimensions of the image
   * in pixels. Additionally, it may have other properties or methods associated with
   * its loading or manipulation.
   * 
   * The `ImageOutputStream` object is an output stream for images that allows for the
   * writing of image data to a file or memory buffer. It provides methods for setting
   * image write parameters and writing image data to the output stream.
   * 
   * The `ImageWriter` class represents an image writer that can be used to write image
   * data to a variety of image file formats. The `ImageWriteParam` class defines the
   * parameters for writing an image, including compression mode and quality.
   * 
   * In summary, the `compressImageToByteStream` function is a method that takes in an
   * `BufferedImage` object and an ` ByteArrayOutputStream` object as input and writes
   * the image data to a memory buffer in a compressed format using an `ImageWriter`.
   * 
   * @param imageByteStream byte array that will be used to store the compressed image
   * data.
   * 
   * 	- The `BufferedImage` parameter `bufferedImage` is converted into an IIOImage
   * object, which serves as the input for the image compression.
   * 	- The `ByteArrayOutputStream` object `imageByteStream` is used to store the
   * compressed image data in a byte array format.
   * 	- The `ImageWriter` instance `imageWriter` is created with the JPEG format, and
   * its default write parameters are retrieved using the `getDefaultWriteParam()` method.
   * 	- The compression mode can be set explicitly using the `setCompressionMode()`
   * method, and the compression quality can be adjusted using the `setCompressionQuality()`
   * method.
   * 	- Finally, the compressed image data is written to the output stream using the
   * `write()` method, and the image writer is disposed of using the `dispose()` method.
   */
  private void compressImageToByteStream(BufferedImage bufferedImage,
      ByteArrayOutputStream imageByteStream) throws IOException {

    try (ImageOutputStream imageOutStream = ImageIO.createImageOutputStream(imageByteStream)) {

      ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
      imageWriter.setOutput(imageOutStream);
      ImageWriteParam param = imageWriter.getDefaultWriteParam();

      if (param.canWriteCompressed()) {
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(compressedImageQuality);
      }
      imageWriter.write(null, new IIOImage(bufferedImage, null, null), param);
      imageWriter.dispose();
    }
  }

  /**
   * reads an image from an input stream provided by a `MultipartFile` object and returns
   * a `BufferedImage`.
   * 
   * @param multipartFile MultipartFile object containing the image data that will be
   * read and returned as a BufferedImage.
   * 
   * 	- `InputStream multipartFileStream`: This is an input stream obtained from the
   * `getInputStream()` method of the `MultipartFile` object. It provides access to the
   * file's contents as a sequence of bytes.
   * 	- The function then uses the `ImageIO` class to read the contents of the input
   * stream and returns a `BufferedImage`.
   * 
   * @returns a `BufferedImage` object containing the image data from the provided
   * Multipart File.
   * 
   * 	- The output is an instance of `BufferedImage`, which represents a raster image.
   * 	- The image has been read from the input stream using `ImageIO.read()` method.
   * 	- The image is stored in memory for further processing or display.
   */
  private BufferedImage getImageFromMultipartFile(MultipartFile multipartFile) throws IOException {
    try (InputStream multipartFileStream = multipartFile.getInputStream()) {
      return ImageIO.read(multipartFileStream);
    }
  }
}

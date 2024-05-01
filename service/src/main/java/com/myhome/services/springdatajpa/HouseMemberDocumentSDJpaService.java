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
 * is responsible for managing and storing documents related to house members. It
 * provides methods for saving and retrieving documents from a repository using JPA.
 * The service also includes methods for compressing images and reading images from
 * multipart files.
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
   * retrieves a House Member Document associated with a given member ID from the
   * repository and maps it to an Optional object.
   * 
   * @param memberId identifier of a member for which the HouseMemberDocument is to be
   * retrieved.
   * 
   * @returns an optional object containing a House Member Document.
   * 
   * 	- `Optional<HouseMemberDocument>`: This represents an optional object containing
   * a HouseMemberDocument or null if no such document exists for the provided member
   * ID.
   * 	- `houseMemberRepository.findByMemberId(memberId)`: This method retrieves a `List`
   * of `HouseMember` objects based on their `memberId`.
   * 	- `map(HouseMember::getHouseMemberDocument)`: This method applies a function to
   * each element in the `List`, which is a `HouseMember` object. The function returns
   * the `HouseMemberDocument` associated with the House Member, if any.
   * 
   * In summary, this function returns an optional object containing the `HouseMemberDocument`
   * associated with a given member ID, or null if no such document exists.
   */
  @Override
  public Optional<HouseMemberDocument> findHouseMemberDocument(String memberId) {
    return houseMemberRepository.findByMemberId(memberId)
        .map(HouseMember::getHouseMemberDocument);
  }

  /**
   * deletes a house member's document by updating the member object and saving it to
   * the repository. If the member had a document, it is set to null before being saved.
   * The function returns `true` if the document was successfully deleted, or `false`
   * otherwise.
   * 
   * @param memberId ID of the member whose House Member Document will be deleted.
   * 
   * @returns a boolean value indicating whether the house member document associated
   * with the given member ID has been successfully deleted.
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
   * updates a House Member Document based on a provided MultipartFile and member ID
   * by finding the corresponding member in the repository, creating or updating the
   * document, and adding it to the member.
   * 
   * @param multipartFile uploaded House Member document to be updated.
   * 
   * 	- `multipartFile`: A `MultipartFile` object containing the updated House Member
   * document in binary format.
   * 	- `memberId`: A `String` representing the ID of the member whose document is being
   * updated.
   * 
   * @param memberId ID of the house member whose document is being updated.
   * 
   * @returns an `Optional<HouseMemberDocument>` containing the updated document for
   * the specified member.
   * 
   * 	- The first element is an `Optional` object containing a `House Member Document`
   * if the update was successful, or `empty` otherwise.
   * 	- The `houseMemberRepository` method returns a `List` of `House Member` objects
   * that match the provided `memberId`.
   * 	- The `map` method applies a function to each element in the `List`, which in
   * this case is a `Try` to create a new `House Member Document` based on the provided
   * `multipartFile` and `memberId`. If the `Try` succeeds, the resulting `House Member
   * Document` is added to the original `House Member` object using the `addDocumentToHouseMember`
   * method.
   * 	- The `orElse` method returns the `Optional` object if the `map` method did not
   * find a matching `House Member` or if the update was unsuccessful.
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
   * creates a new House Member Document or updates an existing one based on a multipart
   * file and member ID. It first retrieves the House Member data using the member ID,
   * then creates or updates the document using the retrieved data and the multipart
   * file, and finally adds the updated document to the House Member data.
   * 
   * @param multipartFile file containing the House Member Document that is being created
   * or updated.
   * 
   * 	- `multipartFile`: A `MultipartFile` object containing the file to be processed.
   * 	+ Properties: `ContentType`, ` filename`, `originalFilename`, `status`, and `byteRange`.
   * 
   * @param memberId identifier of the member for which a HouseMemberDocument is to be
   * created or updated.
   * 
   * @returns an Optional<HouseMemberDocument> containing the created document, or an
   * empty Optional if failed to create the document.
   * 
   * 	- The output is an Optional<HouseMemberDocument>, which means that it may contain
   * a valid House Member Document or may be empty.
   * 	- The Optional.ofValue() method is used to wrap the result of the map() method
   * in an Optional object, indicating that there is a valid House Member Document present.
   * 	- The findByMemberId() method from the houseMemberRepository is used to retrieve
   * a member from the repository based on the provided member ID.
   * 	- The map() method is used to apply a function to the member retrieved from the
   * repository, which in this case creates a new House Member Document using the
   * multipartFile and member parameters.
   * 	- The tryCreateDocument() method is used to create a new House Member Document,
   * and if successful, returns an Optional<HouseMemberDocument>. If there is an error
   * creating the document, the method returns an empty Optional.
   * 	- The addDocumentToHouseMember() method is used to add the newly created House
   * Member Document to the member retrieved from the repository.
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
   * takes a MultipartFile and a HouseMember as input, and attempts to create a document
   * from the file and save it with a unique name. If successful, an Optional<HouseMemberDocument>
   * is returned containing the newly created document.
   * 
   * @param multipartFile file being processed, which contains the image to be saved
   * as a HouseMemberDocument.
   * 
   * 	- `multipartFile`: A `MultipartFile` object representing an image file uploaded
   * by the user.
   * 	- `member`: An instance of the `HouseMember` class, which contains information
   * about a member of a house.
   * 	- `imageByteStream`: A `ByteArrayOutputStream` used to store the image data
   * temporarily during processing.
   * 	- `documentImage`: A `BufferedImage` object containing the image data from the
   * uploaded file.
   * 	- `compressionBorderSizeKBytes`: An `DataSize` object representing the size of
   * the compression border, which is used to determine if the image needs to be compressed.
   * 	- `maxFileSizeKBytes`: An `DataSize` object representing the maximum file size
   * in kilobytes, which is used to determine if the image needs to be compressed.
   * 
   * @param member HouseMember for which a document is to be created.
   * 
   * 	- `member`: A HouseMember object representing a member of a house. It has an `Id`
   * property and various other attributes such as `name`, `email`, `phoneNumber`, etc.
   * 
   * @returns an optional `HouseMemberDocument` object, representing a successfully
   * created document or an empty value if an error occurred.
   * 
   * 	- `Optional<HouseMemberDocument>`: The output is an optional instance of
   * `HouseMemberDocument`, which means that it may be present or absent depending on
   * the success of the function.
   * 	- `House Member Document`: This is the type of the output, which represents a
   * document belonging to a member of a house.
   * 	- `saveHouseMemberDocument()`: This is a method that saves the `ByteArrayOutputStream`
   * containing the image to a file with a specified name, based on the `memberId` of
   * the input `HouseMember` object.
   * 	- `DataSize`: This is an enum class representing different sizes of data, used
   * in the function to determine when the image needs to be compressed.
   * 	- `compressionBorderSizeKBytes`: This is a constant representing the size of the
   * border area around an image that needs to be compressed before it can be saved.
   * 	- `maxFileSizeKBytes`: This is a constant representing the maximum size of a file
   * that can be saved.
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
   * updates a House Member's document and saves it to the repository.
   * 
   * @param houseMemberDocument HouseMember Document object that is being added to the
   * `House Member` object referenced by the `member` parameter.
   * 
   * 	- `HouseMemberDocument`: A class that represents a document related to a House Member.
   * 	- `member`: The House Member object that the document is associated with.
   * 	- `houseMemberRepository`: A repository responsible for storing and retrieving
   * House Member objects.
   * 
   * @param member HouseMember to which the `houseMemberDocument` is being added, and
   * it is updated with the provided `houseMemberDocument`.
   * 
   * 	- `setHouseMemberDocument`: This method sets the `HouseMemberDocument` field of
   * the `member` object to the provided `houseMemberDocument`.
   * 
   * @returns a saved HouseMember object with the associated House Member Document.
   * 
   * 	- The `HouseMember` object is updated by assigning the `HouseMemberDocument`
   * object to its `setHouseMemberDocument()` method.
   * 	- The `HouseMember` object is persisted in the repository by calling the `save()`
   * method, which returns the saved object.
   * 
   * The output of this function can be destructured as follows:
   * 
   * 	- `HouseMember` object that has been updated with the provided `HouseMemberDocument`.
   * 	- The `HouseMemberDocument` object that has been associated with the `HouseMember`.
   */
  private HouseMember addDocumentToHouseMember(HouseMemberDocument houseMemberDocument,
      HouseMember member) {
    member.setHouseMemberDocument(houseMemberDocument);
    return houseMemberRepository.save(member);
  }

  /**
   * saves a `HouseMemberDocument` object to the database, created by combining an image
   * byte stream with a filename and storing them in a `HouseMemberDocument` instance.
   * 
   * @param imageByteStream byte array of an image that is to be saved as a House Member
   * Document.
   * 
   * 	- `ByteArrayOutputStream imageByteStream`: A `ByteArrayOutputStream` is a stream
   * that allows you to write binary data to a byte array. In this case, it is used to
   * store the image data from the `HouseMemberDocument` class.
   * 	- `filename`: The filename of the saved document. This parameter is passed in as
   * a string and is used to determine the name of the document being saved.
   * 
   * @param filename name of the file to which the `HouseMemberDocument` object will
   * be saved.
   * 
   * @returns a new `HouseMemberDocument` object representing the saved document in the
   * repository.
   * 
   * 	- `HouseMemberDocument`: This is the type of object that is returned by the
   * function, which represents a document containing a house member's information.
   * 	- `filename`: This is the name of the file that contains the image data, as passed
   * in as a parameter to the function.
   * 	- `imageByteStream`: This is a byte array containing the image data, which is
   * converted from a ` ByteArrayOutputStream` object and passed as a parameter to the
   * function.
   * 
   * The `saveHouseMemberDocument` function takes these two inputs and returns an
   * instance of `HouseMemberDocument`, which contains information about a house member.
   * The returned document can be used for further processing or storage, such as saving
   * it in a database or file system.
   */
  private HouseMemberDocument saveHouseMemberDocument(ByteArrayOutputStream imageByteStream,
      String filename) {
    HouseMemberDocument newDocument =
        new HouseMemberDocument(filename, imageByteStream.toByteArray());
    return houseMemberDocumentRepository.save(newDocument);
  }

  /**
   * writes a `BufferedImage` object to a `ByteArrayOutputStream` object using the
   * `ImageIO.write` method with the specified image format.
   * 
   * @param documentImage 2D image to be written to a byte stream as a JPEG file.
   * 
   * 	- `BufferedImage documentImage`: A type-safe wrapper class for an image representation
   * in the Java ImageIO API.
   * 	- `ByteArrayOutputStream imageByteStream`: An output stream to write the image
   * data to a byte array.
   * 
   * @param imageByteStream output stream where the image data will be written in binary
   * form.
   * 
   * 	- The `imageByteStream` object is an instance of `ByteArrayOutputStream`, which
   * is used to store the serialized image data in a byte array.
   * 	- The `imageByteStream` has a `size()` attribute that indicates the current size
   * of the stored image data in bytes.
   * 	- The `imageByteStream` has a `toByteArray()` method that returns a `byte[]` array
   * containing the serialized image data.
   */
  private void writeImageToByteStream(BufferedImage documentImage,
      ByteArrayOutputStream imageByteStream)
      throws IOException {
    ImageIO.write(documentImage, "jpg", imageByteStream);
  }

  /**
   * compresses a `BufferedImage` using an `ImageWriter`, setting the compression mode
   * and quality.
   * 
   * @param bufferedImage 2D image to be compressed and written to an output stream as
   * a JPEG file.
   * 
   * 	- `BufferedImage`: represents an image buffered for efficient display or manipulation.
   * It contains information about the image's pixels, dimensions, and encoding.
   * 	- `ImageIO.createImageOutputStream()`: creates an ImageOutputStream that can be
   * used to write an image to a file.
   * 	- `ImageWriter`: an interface for writing images in various formats. It provides
   * methods for setting output streams and image quality parameters.
   * 	- `ImageWriteParam`: defines the compression mode and quality settings for an image.
   * 	- `IIOImage`: represents an IIO (Independent Image Objects) image, which is a
   * container for images that can be read or written using various APIs. It contains
   * information about the image's pixels, dimensions, and encoding.
   * 
   * @param imageByteStream output stream where the compressed image will be written.
   * 
   * 	- `BufferedImage bufferedImage`: The original image to be compressed.
   * 	- `ByteArrayOutputStream imageByteStream`: A byte array output stream used for
   * writing the compressed image data.
   * 	- `IOException` thrown when an I/O error occurs during compression.
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
   * reads an image from an input stream generated by a `MultipartFile` object and
   * returns the resulting `BufferedImage`.
   * 
   * @param multipartFile file that needs to be read and converted into an image.
   * 
   * 	- `InputStream multipartFileStream`: A stream representing the contents of the
   * uploaded file, which can be read using `ImageIO`.
   * 
   * @returns a BufferedImage object containing the contents of the input stream.
   * 
   * 	- The output is an instance of the `BufferedImage` class.
   * 	- It represents the image data read from the input stream using `ImageIO.read()`.
   * 	- The image data is stored in a two-dimensional matrix of pixels, where each pixel
   * is represented by a color value (e.g., RGB).
   * 	- The size of the image is determined by the dimensions of the `BufferedImage` instance.
   */
  private BufferedImage getImageFromMultipartFile(MultipartFile multipartFile) throws IOException {
    try (InputStream multipartFileStream = multipartFile.getInputStream()) {
      return ImageIO.read(multipartFileStream);
    }
  }
}

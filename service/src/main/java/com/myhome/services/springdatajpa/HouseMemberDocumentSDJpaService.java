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
 * provides methods for writing an image to a byte stream and compressing it using
 * an ImageWriter object, as well as reading an image from an input stream provided
 * by a MultipartFile object and returning a BufferedImage.
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
   * maps the result of `houseMemberRepository.findByMemberId(memberId)` to
   * `HouseMemberDocument` by calling the `getHouseMemberDocument` method.
   * 
   * @param memberId unique identifier of a member in the HouseMemberRepository, which
   * is used to retrieve the corresponding `HouseMemberDocument`.
   * 
   * @returns an optional instance of `HouseMemberDocument`.
   * 
   * 	- `Optional<HouseMemberDocument>` is the type of the output, which indicates that
   * the function either returns an instance of `HouseMemberDocument` or `None`.
   * 	- `houseMemberRepository.findByMemberId(memberId)` is a method call that retrieves
   * a `List` of `House Member` objects based on the `memberId` parameter.
   * 	- `.map(HouseMember::getHouseMemberDocument)` is a method call that applies the
   * `getHouseMemberDocument` method to each element in the `List` and returns an
   * `Optional` containing the result. The `getHouseMemberDocument` method is explained
   * below:
   * 
   * The `getHouse MemberDocument` method of the `House Member` class returns a `House
   * Member Document` object, which contains information about the house member, such
   * as their name, address, and other relevant details.
   */
  @Override
  public Optional<HouseMemberDocument> findHouseMemberDocument(String memberId) {
    return houseMemberRepository.findByMemberId(memberId)
        .map(HouseMember::getHouseMemberDocument);
  }

  /**
   * deletes a house member's document by finding the member in the repository, setting
   * their document to null, and saving the updated member to the repository. It returns
   * `true` if successful or `false` otherwise.
   * 
   * @param memberId ID of a house member whose House Member Document is to be deleted.
   * 
   * @returns a boolean value indicating whether the house member document was successfully
   * deleted or not.
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
   * updates a house member's document by retrieving the member from the repository,
   * creating a new document if necessary, and adding it to the member's record.
   * 
   * @param multipartFile file containing the updated House Member document to be
   * processed by the `updateHouseMemberDocument()` method.
   * 
   * 	- `multipartFile`: A `MultipartFile` object representing a file uploaded by the
   * user. It has several attributes such as `getOriginalFilename()`, `getSize()`,
   * `getContentType()`, and `getBytes()`.
   * 
   * @param memberId unique identifier of the member whose House Member Document will
   * be updated.
   * 
   * @returns an optional `House Member Document` object representing the updated
   * document for the specified member.
   * 
   * 	- `Optional<HouseMemberDocument>`: The output is an optional reference to a
   * `HouseMemberDocument`, indicating that it may or may not be present depending on
   * the result of the operation.
   * 	- `houseMemberRepository.findByMemberId(memberId)`: This is a call to the
   * `houseMemberRepository` method `findByMemberId`, which retrieves a `HouseMember`
   * object based on the `memberId` parameter. The method returns an `Optional<HouseMember>`
   * reference, indicating that the object may or may not be present in the repository.
   * 	- `map(member -> { ... })`: This is a call to the `map` method of the `Optional`
   * reference, which applies the provided function to the contained object and returns
   * a new `Optional` reference. In this case, the function is a closure that creates
   * a new `HouseMemberDocument` instance based on the `multipartFile` and `member`
   * parameters, and then adds the document to the `HouseMember` object using the
   * `addDocumentToHouse Member` method.
   * 	- `orElse(Optional.empty())`: This is a call to the `orElse` method of the
   * `Optional` reference, which returns a new `Optional` reference if the contained
   * object is present, or an empty reference if it is absent. In this case, the function
   * is called with an empty `Optional` reference as the default value, indicating that
   * if no `HouseMemberDocument` instance is found in the repository, the output will
   * be an empty reference.
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
   * creates a new `HouseMemberDocument` if it doesn't exist for the given `memberId`.
   * It first queries the `houseMemberRepository` to retrieve the member record, then
   * tries to create the document using the provided multipart file. If successful, it
   * adds the document to the member record and returns the resulting `HouseMemberDocument`.
   * Otherwise, it returns an empty `Optional`.
   * 
   * @param multipartFile file containing the House Member Document to be created or updated.
   * 
   * 	- `multipartFile`: A `MultipartFile` object containing the uploaded file data.
   * The file type is determined by its content type header, which can be retrieved
   * through the `getContentType()` method.
   * 
   * @param memberId ID of the member for which the House Member Document is to be created.
   * 
   * @returns an `Optional` instance containing a `HouseMemberDocument` object, created
   * by merging the provided multipart file with the specified member ID.
   * 
   * 	- The first element is an Optional<HouseMemberDocument>, indicating that the
   * method may return either a HouseMemberDocument or an empty Optional.
   * 	- The `findByMemberId` method call in the `map` block returns an Optional<HouseMember>
   * representing a member with the specified ID.
   * 	- The `tryCreateDocument` method creates a new HouseMemberDocument for the provided
   * multipartFile and member, or returns an empty Optional if creation failed.
   * 	- The `addDocumentToHouse Member` method adds the created HouseMemberDocument to
   * the member's list of documents.
   * 	- The `orElse` method is used to return an emptyOptional if no HouseMemberDocument
   * could be created for the provided multipartFile and member.
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
   * creates a document for a HouseMember by reading an image from a MultipartFile,
   * compressing it if necessary, and saving it as a JPEG file. If successful, it returns
   * an Optional<HouseMemberDocument>.
   * 
   * @param multipartFile multipart file containing an image of a member's document,
   * which is being processed and converted into a byte array for storage or further processing.
   * 
   * 	- `multipartFile`: A `MultipartFile` object representing a file uploaded by the
   * user in the HTML form.
   * 	- `member`: An instance of `HouseMember` class, which contains information about
   * the member who is uploading the document.
   * 
   * @param member HouseMember for which an image document will be created.
   * 
   * 	- `member`: The HouseMember object that contains information about a member of a
   * house.
   * 	- `multipartFile`: A MultipartFile object containing an image file related to the
   * member.
   * 	- `imageByteStream`: A ByteArrayOutputStream instance used to store the image data.
   * 	- `documentImage`: A BufferedImage object containing the image data read from the
   * input file.
   * 	- `compressionBorderSizeKBytes`: The size of the compression border in kilobytes,
   * which is used to determine if the image needs to be compressed.
   * 	- `maxFileSizeKBytes`: The maximum file size in kilobytes, which is used to
   * determine if the image needs to be compressed or saved directly.
   * 	- `saveHouseMemberDocument`: A method that saves the image data to a file with a
   * specified name.
   * 
   * @returns an optional `HouseMemberDocument`, which represents a document created
   * from a member's image.
   * 
   * 	- The `Optional` object contains a `HouseMemberDocument` element, which is created
   * by saving the image to a file using the `saveHouseMemberDocument` method.
   * 	- The `HouseMemberDocument` element has a `memberId` attribute, which is the ID
   * of the member whose document was created.
   * 	- The `imageByteStream` attribute contains the binary data of the compressed image
   * file.
   * 	- The `maxFileSizeKBytes` attribute represents the maximum size of the file that
   * can be saved in bytes.
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
   * updates a `HouseMember` object's `HouseMemberDocument` field and saves it to the
   * repository, effectively linking the member with the document.
   * 
   * @param houseMemberDocument HouseMember's document that is being added to the
   * member's record.
   * 
   * 	- `HouseMemberDocument`: represents a document related to a House Member
   * 	- `houseMember`: references a specific House Member object
   * 	- `save()`: saves the updated House Member object in the repository
   * 
   * @param member HouseMember object that will have its `HouseMemberDocument` attribute
   * set to the provided `HouseMemberDocument` object and then saved in the repository.
   * 
   * 	- `member`: A `HouseMember` object that represents a member of a house.
   * 	- `houseMemberDocument`: A `HouseMemberDocument` object that contains information
   * about the member and their documents.
   * 
   * @returns a saved HouseMember object with the associated HouseMemberDocument.
   * 
   * 	- `houseMemberRepository`: This is an instance of `HouseMemberRepository`, which
   * is responsible for managing HouseMembers and their associated documents.
   * 	- `save()`: This method is used to save or update a House Member in the repository.
   * It returns the saved House Member object.
   */
  private HouseMember addDocumentToHouseMember(HouseMemberDocument houseMemberDocument,
      HouseMember member) {
    member.setHouseMemberDocument(houseMemberDocument);
    return houseMemberRepository.save(member);
  }

  /**
   * saves a HouseMemberDocument to the repository, taking an image byte stream and
   * filename as input and returning the newly created document.
   * 
   * @param imageByteStream image data of the House Member document to be saved, which
   * is converted into a ByteArrayOutputStream and then stored in the repository along
   * with the filename.
   * 
   * 	- ` ByteArrayOutputStream imageByteStream`: Represents an output stream that
   * writes bytes to a byte array.
   * 	- `filename`: The name of the file to which the `imageByteStream` contents will
   * be written.
   * 
   * @param filename file name of the image being saved.
   * 
   * @returns a newly created `HouseMemberDocument` object, which is then saved to the
   * repository.
   * 
   * The HouseMemberDocument object returned by the function represents a new document
   * added to the repository. The document's filename and image byte array are obtained
   * from the input parameters.
   * 
   * The `houseMemberDocumentRepository.save()` method is called to persist the document
   * in the repository, ensuring its persistence and availability in the system.
   */
  private HouseMemberDocument saveHouseMemberDocument(ByteArrayOutputStream imageByteStream,
      String filename) {
    HouseMemberDocument newDocument =
        new HouseMemberDocument(filename, imageByteStream.toByteArray());
    return houseMemberDocumentRepository.save(newDocument);
  }

  /**
   * writes a `BufferedImage` to a byte stream using the `ImageIO.write` method and
   * specifying "jpg" as the image format.
   * 
   * @param documentImage 2D graphics or other image data to be written to a byte stream
   * using the `ImageIO.write()` method.
   * 
   * The `BufferedImage` object `documentImage` represents an image that has been loaded
   * from a file or some other source. This image can have any format like JPEG, PNG,
   * GIF, TIFF, BMP, etc.
   * The `ByteArrayOutputStream` variable `imageByteStream` serves as a container to
   * hold the binary data of the written image. It is used to store the output from the
   * `ImageIO.write()` method in a byte array.
   * 
   * @param imageByteStream ByteArrayOutputStream where the image will be written to.
   * 
   * 	- It is an instance of `ByteArrayOutputStream`, which is a class in Java for
   * converting a byte stream into an array of bytes.
   * 	- The class has several attributes, including `count`, `empty`, `toByteArray()`,
   * and `writeTo()` methods.
   */
  private void writeImageToByteStream(BufferedImage documentImage,
      ByteArrayOutputStream imageByteStream)
      throws IOException {
    ImageIO.write(documentImage, "jpg", imageByteStream);
  }

  /**
   * compresses an input `BufferedImage` using the JPEG compression algorithm and writes
   * the compressed data to a `ByteArrayOutputStream`.
   * 
   * @param bufferedImage 2D image to be compressed and is used by the `ImageWriter`
   * to write the compressed image to a byte stream.
   * 
   * 	- The `BufferedImage` object represents an image that has been loaded from an
   * external source and buffered for efficient access.
   * 	- The `ImageIO` class is used to read and write image files in various formats,
   * including JPEG.
   * 	- The `ImageOutputStream` object is a stream that can be used to write an image
   * file. It is created by calling the `ImageIO.createImageOutputStream()` method and
   * passed as a parameter to the ` ImageWriter` constructor.
   * 	- The `ImageWriter` class is responsible for writing an image file to a stream.
   * It takes a `BufferedImage` object as input, modifies its properties according to
   * user preferences, and writes the modified image to the output stream using the
   * appropriate image format.
   * 
   * @param imageByteStream 10-byte stream where the compressed image will be written.
   * 
   * 1/ It is a `ByteArrayOutputStream`, which means it is an output stream that stores
   * data in a byte array.
   * 2/ It is created using the `ImageIO.createImageOutputStream()` method, indicating
   * that it is used for writing image data.
   * 3/ It has various attributes, such as its size, buffer size, and whether it is
   * resetable or not, which are not explicitly stated in the code snippet provided.
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
   * returns a `BufferedImage`.
   * 
   * @param multipartFile file that is being uploaded through a multipart form, and it
   * is used to retrieve the image data from the file using `ImageIO.read()`.
   * 
   * 	- `InputStream multipartFileStream`: This is an input stream obtained from the
   * `getInputStream` method of the `MultipartFile` object.
   * 	- `ImageIO.read()`: This is a method that reads an image from the input stream
   * and returns a `BufferedImage`.
   * 
   * @returns a buffered image object read from an input stream.
   * 
   * 	- The input stream is obtained from the `MultipartFile` object using the
   * `getInputStream()` method.
   * 	- The `ImageIO` class is used to read the image data from the input stream.
   * 	- The resulting image is a `BufferedImage` object, which represents an image that
   * can be displayed or manipulated in various ways.
   */
  private BufferedImage getImageFromMultipartFile(MultipartFile multipartFile) throws IOException {
    try (InputStream multipartFileStream = multipartFile.getInputStream()) {
      return ImageIO.read(multipartFileStream);
    }
  }
}

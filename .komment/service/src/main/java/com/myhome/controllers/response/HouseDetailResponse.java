{"name":"HouseDetailResponse.java","path":"service/src/main/java/com/myhome/controllers/response/HouseDetailResponse.java","content":{"structured":{"description":"A class called `HouseDetailResponse` that contains a single instance field called `house` of type `CommunityHouseDto`. The class is marked with various annotations, including `@NoArgsConstructor`, `@AllArgsConstructor`, and `@Data`, which indicate the use of automated construction and data annotations. The class is also imported from the `lombok` package, which provides a range of convenience annotations for Java development.","items":[{"id":"195424b6-46f1-8d82-f54d-1d75d55ebcea","ancestors":[],"type":"function","description":"encapsulates a CommunityHouseDto object for storing and retrieving house details.\nFields:\n\t- house (CommunityHouseDto): in the HouseDetailResponse class contains an instance of the CommunityHouseDto class, which likely represents a detailed summary of a specific house.\n\n","fields":[{"name":"house","type_name":"CommunityHouseDto","value":null,"constant":false,"class_name":"HouseDetailResponse","description":"in the HouseDetailResponse class contains an instance of the CommunityHouseDto class, which likely represents a detailed summary of a specific house."}],"name":"HouseDetailResponse","code":"@NoArgsConstructor\n@AllArgsConstructor\n@Data\npublic class HouseDetailResponse {\n  private CommunityHouseDto house;\n}","location":{"start":24,"insert":24,"offset":" ","indent":0,"comment":null},"item_type":"class","length":6}]}}}
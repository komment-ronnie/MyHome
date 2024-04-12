{"name":"HouseController.java","path":"service/src/main/java/com/myhome/controllers/HouseController.java","content":{"structured":{"description":"A `HouseController` class that implements the `HousesApi` interface. The controller handles requests related to houses, including listing all houses, getting details of a specific house, adding members to a house, and deleting members from a house. The code uses various Spring packages such as `@RestController`, `@RequiredArgsConstructor`, `@Slf4j`, `PageableDefault`, `ResponseEntity`, and `List<CommunityHouse>`.","image":"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<!-- Generated by graphviz version 2.43.0 (0)\n -->\n<!-- Title: com.myhome.controllers.HouseController Pages: 1 -->\n<svg width=\"181pt\" height=\"93pt\"\n viewBox=\"0.00 0.00 181.00 93.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1 1) rotate(0) translate(4 89)\">\n<title>com.myhome.controllers.HouseController</title>\n<!-- Node1 -->\n<g id=\"Node000001\" class=\"node\">\n<title>Node1</title>\n<g id=\"a_Node000001\"><a xlink:title=\" \">\n<polygon fill=\"#999999\" stroke=\"#666666\" points=\"173,-30 0,-30 0,0 173,0 173,-30\"/>\n<text text-anchor=\"start\" x=\"8\" y=\"-18\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.controllers.House</text>\n<text text-anchor=\"middle\" x=\"86.5\" y=\"-7\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">Controller</text>\n</a>\n</g>\n</g>\n<!-- Node2 -->\n<g id=\"Node000002\" class=\"node\">\n<title>Node2</title>\n<g id=\"a_Node000002\"><a xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"120.5,-85 52.5,-85 52.5,-66 120.5,-66 120.5,-85\"/>\n<text text-anchor=\"middle\" x=\"86.5\" y=\"-73\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">HousesApi</text>\n</a>\n</g>\n</g>\n<!-- Node2&#45;&gt;Node1 -->\n<g id=\"edge1_Node000001_Node000002\" class=\"edge\">\n<title>Node2&#45;&gt;Node1</title>\n<g id=\"a_edge1_Node000001_Node000002\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M86.5,-55.65C86.5,-47.36 86.5,-37.78 86.5,-30.11\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"83,-55.87 86.5,-65.87 90,-55.87 83,-55.87\"/>\n</a>\n</g>\n</g>\n</g>\n</svg>\n","items":[{"id":"fb24d790-e863-9db6-7443-7b61bd048c1b","ancestors":[],"type":"function","description":"is responsible for handling requests related to houses and their members. It provides endpoints for listing all houses, getting details of a specific house, adding members to a house, and deleting members from a house. The controller uses dependencies such as the HouseMemberMapper and the HouseService to perform these operations.","name":"HouseController","code":"@RestController\n@RequiredArgsConstructor\n@Slf4j\npublic class HouseController implements HousesApi {\n  private final HouseMemberMapper houseMemberMapper;\n  private final HouseService houseService;\n  private final HouseApiMapper houseApiMapper;\n\n  @Override\n  public ResponseEntity<GetHouseDetailsResponse> listAllHouses(\n      @PageableDefault(size = 200) Pageable pageable) {\n    log.trace(\"Received request to list all houses\");\n\n    Set<CommunityHouse> houseDetails =\n        houseService.listAllHouses(pageable);\n    Set<GetHouseDetailsResponseCommunityHouse> getHouseDetailsResponseSet =\n        houseApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(houseDetails);\n\n    GetHouseDetailsResponse response = new GetHouseDetailsResponse();\n\n    response.setHouses(getHouseDetailsResponseSet);\n\n    return ResponseEntity.status(HttpStatus.OK).body(response);\n  }\n\n  @Override\n  public ResponseEntity<GetHouseDetailsResponse> getHouseDetails(String houseId) {\n    log.trace(\"Received request to get details of a house with id[{}]\", houseId);\n    return houseService.getHouseDetailsById(houseId)\n        .map(houseApiMapper::communityHouseToRestApiResponseCommunityHouse)\n        .map(Collections::singleton)\n        .map(getHouseDetailsResponseCommunityHouses -> new GetHouseDetailsResponse().houses(getHouseDetailsResponseCommunityHouses))\n        .map(ResponseEntity::ok)\n        .orElse(ResponseEntity.notFound().build());\n  }\n\n  @Override\n  public ResponseEntity<ListHouseMembersResponse> listAllMembersOfHouse(\n      String houseId,\n      @PageableDefault(size = 200) Pageable pageable) {\n    log.trace(\"Received request to list all members of the house with id[{}]\", houseId);\n\n    return houseService.getHouseMembersById(houseId, pageable)\n        .map(HashSet::new)\n        .map(houseMemberMapper::houseMemberSetToRestApiResponseHouseMemberSet)\n        .map(houseMembers -> new ListHouseMembersResponse().members(houseMembers))\n        .map(ResponseEntity::ok)\n        .orElse(ResponseEntity.notFound().build());\n  }\n\n  @Override\n  public ResponseEntity<AddHouseMemberResponse> addHouseMembers(\n      @PathVariable String houseId, @Valid AddHouseMemberRequest request) {\n\n    log.trace(\"Received request to add member to the house with id[{}]\", houseId);\n    Set<HouseMember> members =\n        houseMemberMapper.houseMemberDtoSetToHouseMemberSet(request.getMembers());\n    Set<HouseMember> savedHouseMembers = houseService.addHouseMembers(houseId, members);\n\n    if (savedHouseMembers.size() == 0 && request.getMembers().size() != 0) {\n      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();\n    } else {\n      AddHouseMemberResponse response = new AddHouseMemberResponse();\n      response.setMembers(\n          houseMemberMapper.houseMemberSetToRestApiResponseAddHouseMemberSet(savedHouseMembers));\n      return ResponseEntity.status(HttpStatus.CREATED).body(response);\n    }\n  }\n\n  @Override\n  public ResponseEntity<Void> deleteHouseMember(String houseId, String memberId) {\n    log.trace(\"Received request to delete a member from house with house id[{}] and member id[{}]\",\n        houseId, memberId);\n    boolean isMemberDeleted = houseService.deleteMemberFromHouse(houseId, memberId);\n    if (isMemberDeleted) {\n      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();\n    } else {\n      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();\n    }\n  }\n}","location":{"start":43,"insert":43,"offset":" ","indent":0,"comment":null},"item_type":"class","length":81},{"id":"e0914680-e005-738e-c74d-7574c8d6421d","ancestors":["fb24d790-e863-9db6-7443-7b61bd048c1b"],"type":"function","description":"receives a pageable request from the client and list all houses from the service, then maps them to the REST API response format using the provided mapper, and returns the response to the client.","params":[{"name":"pageable","type_name":"Pageable","description":"page size and sort order for listing all houses.\n\n* `@PageableDefault(size = 200)` - This annotation sets the default page size for listings to 200.","complex_type":true}],"returns":{"type_name":"GetHouseDetailsResponse","description":"a list of `GetHouseDetailsResponseCommunityHouseSet`.\n\n* `response`: This is the main output of the function, which is a `GetHouseDetailsResponse` object.\n* `setHouses`: This is a set of `CommunityHouse` objects, which are the details of each house listed in the response.\n* `pageable`: This is an optional parameter that represents the page size and sort order for the list of houses.\n* `houseService`: This is the service used to retrieve the list of houses.\n* `houseApiMapper`: This is the mapper used to transform the list of `CommunityHouse` objects into a set of `GetHouseDetailsResponseCommunityHouse` objects.","complex_type":true},"usage":{"language":"java","code":"@Override\npublic ResponseEntity<GetHouseDetailsResponse> listAllHouses(@PageableDefault(size = 200) Pageable pageable) {\n    log.trace(\"Received request to list all houses\");\n\n    Set<CommunityHouse> houseDetails =\n            houseService.listAllHouses(pageable);\n    Set<GetHouseDetailsResponseCommunityHouse> getHouseDetailsResponseSet =\n            houseApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(houseDetails);\n\n    GetHouseDetailsResponse response = new GetHouseDetailsResponse();\n\n    response.setHouses(getHouseDetailsResponseSet);\n\n    return ResponseEntity.status(HttpStatus.OK).body(response);\n}\n","description":""},"name":"listAllHouses","code":"@Override\n  public ResponseEntity<GetHouseDetailsResponse> listAllHouses(\n      @PageableDefault(size = 200) Pageable pageable) {\n    log.trace(\"Received request to list all houses\");\n\n    Set<CommunityHouse> houseDetails =\n        houseService.listAllHouses(pageable);\n    Set<GetHouseDetailsResponseCommunityHouse> getHouseDetailsResponseSet =\n        houseApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(houseDetails);\n\n    GetHouseDetailsResponse response = new GetHouseDetailsResponse();\n\n    response.setHouses(getHouseDetailsResponseSet);\n\n    return ResponseEntity.status(HttpStatus.OK).body(response);\n  }","location":{"start":51,"insert":51,"offset":" ","indent":2,"comment":null},"item_type":"method","length":16},{"id":"348c07d0-7834-deb9-0d4e-c453012c8883","ancestors":["fb24d790-e863-9db6-7443-7b61bd048c1b"],"type":"function","description":"receives a house ID and returns a `GetHouseDetailsResponse` object with a list of houses matching the provided ID. It uses service-level methods to retrieve the details and map them to a rest API response.","params":[{"name":"houseId","type_name":"String","description":"unique identifier of the house for which details are requested, and it is used to retrieve the corresponding house details from the service.","complex_type":false}],"returns":{"type_name":"ResponseEntity","description":"a `GetHouseDetailsResponse` object containing a list of houses with their details.\n\n* `ResponseEntity<GetHouseDetailsResponse>`: This is a generic type that represents an entity with a response body containing a `GetHouseDetailsResponse` object.\n* `GetHouseDetailsResponse`: This class represents the response body of the entity, which contains a list of `CommunityHouse` objects.\n* `CommunityHouse`: This class represents a single house in the community, with attributes such as id, name, and location.\n* `map(Function<T, R> mapper)`: This method applies a mapping function to the output of the `getHouseDetails` method, which transforms the response body into a new form. In this case, the function maps each `CommunityHouse` object to a `GetHouseDetailsResponse` object.\n* `map(Supplier<T> supplier)`: This method returns a stream of `T` objects, where `T` is the type of the output of the `getHouseDetails` method. In this case, the supplier returns an empty stream, which means that the output of the method will be an empty list.\n* `orElse(T otherValue)`: This method returns a new response entity if the result of the previous mapping operation is not present, or the specified `otherValue` otherwise. In this case, if the `getHouseDetails` method does not return a response body, the resulting entity will be an `ResponseEntity.notFound().build()`.","complex_type":true},"usage":{"language":"java","code":"GetHouseDetailsResponse response = houseController.getHouseDetails(\"123\"); // \"123\" being the id of the requested house\n","description":""},"name":"getHouseDetails","code":"@Override\n  public ResponseEntity<GetHouseDetailsResponse> getHouseDetails(String houseId) {\n    log.trace(\"Received request to get details of a house with id[{}]\", houseId);\n    return houseService.getHouseDetailsById(houseId)\n        .map(houseApiMapper::communityHouseToRestApiResponseCommunityHouse)\n        .map(Collections::singleton)\n        .map(getHouseDetailsResponseCommunityHouses -> new GetHouseDetailsResponse().houses(getHouseDetailsResponseCommunityHouses))\n        .map(ResponseEntity::ok)\n        .orElse(ResponseEntity.notFound().build());\n  }","location":{"start":68,"insert":68,"offset":" ","indent":2,"comment":null},"item_type":"method","length":10},{"id":"a6d9b5a1-76c7-7aac-f14d-0ff64e04fd68","ancestors":["fb24d790-e863-9db6-7443-7b61bd048c1b"],"type":"function","description":"retrieves the members of a house with a given ID and returns them as a list of `HouseMember` objects in a `ListHouseMembersResponse` message.","params":[{"name":"houseId","type_name":"String","description":"unique identifier of the house for which members are to be listed.","complex_type":false},{"name":"pageable","type_name":"Pageable","description":"page request parameters, such as the page number and size of the result set, which are used to filter and limit the response from the `houseService.getHouseMembersById()` method.\n\n* `size`: The number of elements to be returned in each page of results.\n* `sort`: The field by which the results should be sorted.\n* `direction`: The direction of sorting (ascending or descending).","complex_type":true}],"returns":{"type_name":"ResponseEntity","description":"a `ListHouseMembersResponse` object containing the list of members of the specified house.\n\n* `ResponseEntity<ListHouseMembersResponse>`: This is the type of the output returned by the function, which represents a response entity containing a list of members of a house.\n* `ListHouseMembersResponse`: This is a class that contains properties related to the list of members of a house. The properties include:\n\t+ `members`: A list of `HouseMember` objects, representing the members of the house.\n* `ok`: This is a boolean property indicating whether the response was successful or not. If the response was not successful, the value of this property will be `false`.","complex_type":true},"usage":{"language":"java","code":"ListHouseMembersResponse response = houseController.listAllMembersOfHouse(\"12345\", pageable);\nif (response.getStatusCode().equals(HttpStatus.OK)) {\n    // Handle successful response\n} else if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {\n    // Handle error\n}\n","description":""},"name":"listAllMembersOfHouse","code":"@Override\n  public ResponseEntity<ListHouseMembersResponse> listAllMembersOfHouse(\n      String houseId,\n      @PageableDefault(size = 200) Pageable pageable) {\n    log.trace(\"Received request to list all members of the house with id[{}]\", houseId);\n\n    return houseService.getHouseMembersById(houseId, pageable)\n        .map(HashSet::new)\n        .map(houseMemberMapper::houseMemberSetToRestApiResponseHouseMemberSet)\n        .map(houseMembers -> new ListHouseMembersResponse().members(houseMembers))\n        .map(ResponseEntity::ok)\n        .orElse(ResponseEntity.notFound().build());\n  }","location":{"start":79,"insert":79,"offset":" ","indent":2,"comment":null},"item_type":"method","length":13},{"id":"17c8d09a-9260-d688-ce43-fcdf9a40978d","ancestors":["fb24d790-e863-9db6-7443-7b61bd048c1b"],"type":"function","description":"takes a house ID and a request with member details, adds the members to the house, and returns the updated member list in the response.","params":[{"name":"houseId","type_name":"String","description":"identifier of the house to which the members will be added.","complex_type":false},{"name":"request","type_name":"AddHouseMemberRequest","description":"AddHouseMemberRequest object that contains the member details to be added to the house.\n\n* `houseId`: A string representing the unique identifier of the house to which members will be added.\n* `request.getMembers()`: An array of `AddHouseMemberRequest.Members` objects containing the details of the members to be added to the house. Each `Members` object has the following properties:\n\t+ `member`: A string representing the unique identifier of the member to be added.\n\t+ `email`: A string representing the email address of the member.\n\t+ `firstName`: A string representing the first name of the member.\n\t+ `lastName`: A string representing the last name of the member.\n\t+ `phoneNumber`: A string representing the phone number of the member.\n\nIn summary, the `addHouseMembers` function takes a house ID and a list of members to be added to that house, processes them, and returns a response indicating whether the operation was successful or not.","complex_type":true}],"returns":{"type_name":"AddHouseMemberResponse","description":"a `ResponseEntity` object containing the response to the request, which includes the added house members in a JSON format.\n\n* `response`: This is an instance of the `AddHouseMemberResponse` class, which contains information about the added members.\n* `members`: This is a set of `HouseMember` objects, which represent the added members to the house.\n* `size`: The size of the `members` set, indicating the number of added members.\n\nThe output is structured in the following way:\n\n{\nresponse: {\nmembers: [...],\nsize: 3\n}\n}\n\nWhere `[...]` represents the contents of the `members` set. The `size` property indicates the number of added members.","complex_type":true},"usage":{"language":"java","code":"// This example uses the method addHouseMembers to add multiple members to a house\nAddHouseMemberRequest request = new AddHouseMemberRequest();\nList<HouseMemberDto> membersToBeAdded = new ArrayList<>();\nmembersToBeAdded.add(new HouseMemberDto(\"memberId1\", \"role\"));\nmembersToBeAdded.add(new HouseMemberDto(\"memberId2\", \"role\"));\nrequest.setMembers(membersToBeAdded);\nResponseEntity<AddHouseMemberResponse> response = houseService.addHouseMembers(\"houseId\", request);\nif (response.getStatusCode().equals(HttpStatus.CREATED)) {\n  AddHouseMemberResponse addHouseMemberResponse = response.getBody();\n  List<HouseMember> addedHouseMembers = addHouseMemberResponse.getMembers();\n  for (HouseMember houseMember : addedHouseMembers) {\n    System.out.println(\"Added member with id: \" + houseMember.getId());\n  }\n} else if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {\n  System.out.println(\"At least one of the members could not be added to the house\");\n}\n","description":""},"name":"addHouseMembers","code":"@Override\n  public ResponseEntity<AddHouseMemberResponse> addHouseMembers(\n      @PathVariable String houseId, @Valid AddHouseMemberRequest request) {\n\n    log.trace(\"Received request to add member to the house with id[{}]\", houseId);\n    Set<HouseMember> members =\n        houseMemberMapper.houseMemberDtoSetToHouseMemberSet(request.getMembers());\n    Set<HouseMember> savedHouseMembers = houseService.addHouseMembers(houseId, members);\n\n    if (savedHouseMembers.size() == 0 && request.getMembers().size() != 0) {\n      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();\n    } else {\n      AddHouseMemberResponse response = new AddHouseMemberResponse();\n      response.setMembers(\n          houseMemberMapper.houseMemberSetToRestApiResponseAddHouseMemberSet(savedHouseMembers));\n      return ResponseEntity.status(HttpStatus.CREATED).body(response);\n    }\n  }","location":{"start":93,"insert":93,"offset":" ","indent":2,"comment":null},"item_type":"method","length":18},{"id":"322d0c58-7de3-99af-db4f-e394aced552e","ancestors":["fb24d790-e863-9db6-7443-7b61bd048c1b"],"type":"function","description":"deletes a member from a house based on the house ID and member ID provided in the request. If the member is successfully deleted, a `NO_CONTENT` status code is returned. If the member cannot be found, a `NOT_FOUND` status code is returned.","params":[{"name":"houseId","type_name":"String","description":"ID of the house for which a member is being deleted.","complex_type":false},{"name":"memberId","type_name":"String","description":"ID of the member to be deleted from the specified house.","complex_type":false}],"returns":{"type_name":"ResponseEntity","description":"a `ResponseEntity` object with a status code of either `NO_CONTENT` or `NOT_FOUND`, depending on whether the member was successfully deleted or not.\n\n* `ResponseEntity`: This is an object that represents the response to the delete request. It has a `status` field that indicates the HTTP status code of the response, and a `body` field that contains the response entity itself.\n* `HttpStatus`: This is an enum that defines the possible HTTP status codes that can be returned by the function. The function returns `NO_CONTENT` if the member was successfully deleted, and `NOT_FOUND` otherwise.\n* ` Void`: This is a type parameter of the `ResponseEntity` class, which represents the void value returned by the function.","complex_type":true},"usage":{"language":"java","code":"public static void main(String[] args) {\n    ResponseEntity<Void> response = new HouseController().deleteHouseMember(\"houseId\", \"memberId\");\n}\n","description":""},"name":"deleteHouseMember","code":"@Override\n  public ResponseEntity<Void> deleteHouseMember(String houseId, String memberId) {\n    log.trace(\"Received request to delete a member from house with house id[{}] and member id[{}]\",\n        houseId, memberId);\n    boolean isMemberDeleted = houseService.deleteMemberFromHouse(houseId, memberId);\n    if (isMemberDeleted) {\n      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();\n    } else {\n      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();\n    }\n  }","location":{"start":112,"insert":112,"offset":" ","indent":2,"comment":null},"item_type":"method","length":11}]}}}
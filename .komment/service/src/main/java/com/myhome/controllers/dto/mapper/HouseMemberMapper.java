{"name":"HouseMemberMapper.java","path":"service/src/main/java/com/myhome/controllers/dto/mapper/HouseMemberMapper.java","content":{"structured":{"description":"An interface named `HouseMemberMapper` that serves as a mapping between two data structures: `Set<HouseMember>` and `Set<HouseMemberDto>`. The interface has three methods: `houseMemberSetToRestApiResponseHouseMemberSet()`, `houseMemberDtoSetToHouse MemberSet()`, and `houseMemberSetToRestApiResponseAddHouseMemberSet()`. These methods map the `HouseMember` objects in the input sets to corresponding objects in the output sets, using the `Mapper` package.","items":[{"id":"93859da3-7f01-a5a9-a148-d14e6df0f198","ancestors":[],"type":"function","description":"defines a set of methods for mapping between sets of HouseMembers and related data structures, including converting between HouseMember objects and their corresponding DTOs, as well as adding new HouseMembers to a response.","name":"HouseMemberMapper","code":"@Mapper\npublic interface HouseMemberMapper {\n  Set<com.myhome.model.HouseMember> houseMemberSetToRestApiResponseHouseMemberSet(\n      Set<HouseMember> houseMemberSet);\n\n  Set<HouseMember> houseMemberDtoSetToHouseMemberSet(Set<HouseMemberDto> houseMemberDtoSet);\n\n  Set<com.myhome.model.HouseMember> houseMemberSetToRestApiResponseAddHouseMemberSet(\n      Set<HouseMember> houseMemberSet);\n}","location":{"start":24,"insert":24,"offset":" ","indent":0,"comment":null},"item_type":"interface","length":10}]}}}
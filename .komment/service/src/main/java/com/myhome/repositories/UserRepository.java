{"name":"UserRepository.java","path":"service/src/main/java/com/myhome/repositories/UserRepository.java","content":{"structured":{"description":"An interface called UserRepository, which extends the JpaRepository interface from Spring Data. It provides several methods for interacting with a User entity in a database, including finding a user by email or user ID, finding a user's communities or tokens, and listing all users in a community with pagination. The code uses packages from Spring Data, such as EntityGraph and Query, to define the repository methods and interact with the database.","image":"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<!-- Generated by graphviz version 2.43.0 (0)\n -->\n<!-- Title: com.myhome.domain.User Pages: 1 -->\n<svg width=\"187pt\" height=\"137pt\"\n viewBox=\"0.00 0.00 187.00 137.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1 1) rotate(0) translate(4 133)\">\n<title>com.myhome.domain.User</title>\n<!-- Node1 -->\n<g id=\"Node000001\" class=\"node\">\n<title>Node1</title>\n<g id=\"a_Node000001\"><a xlink:title=\" \">\n<polygon fill=\"#999999\" stroke=\"#666666\" points=\"164,-19 15,-19 15,0 164,0 164,-19\"/>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-7\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.domain.User</text>\n</a>\n</g>\n</g>\n<!-- Node2 -->\n<g id=\"Node000002\" class=\"node\">\n<title>Node2</title>\n<g id=\"a_Node000002\"><a xlink:href=\"classcom_1_1myhome_1_1domain_1_1BaseEntity.html\" target=\"_top\" xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"179,-74 0,-74 0,-55 179,-55 179,-74\"/>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-62\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.domain.BaseEntity</text>\n</a>\n</g>\n</g>\n<!-- Node2&#45;&gt;Node1 -->\n<g id=\"edge1_Node000001_Node000002\" class=\"edge\">\n<title>Node2&#45;&gt;Node1</title>\n<g id=\"a_edge1_Node000001_Node000002\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M89.5,-44.66C89.5,-35.93 89.5,-25.99 89.5,-19.09\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"86,-44.75 89.5,-54.75 93,-44.75 86,-44.75\"/>\n</a>\n</g>\n</g>\n<!-- Node3 -->\n<g id=\"Node000003\" class=\"node\">\n<title>Node3</title>\n<g id=\"a_Node000003\"><a xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"126.5,-129 52.5,-129 52.5,-110 126.5,-110 126.5,-129\"/>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-117\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">Serializable</text>\n</a>\n</g>\n</g>\n<!-- Node3&#45;&gt;Node2 -->\n<g id=\"edge2_Node000002_Node000003\" class=\"edge\">\n<title>Node3&#45;&gt;Node2</title>\n<g id=\"a_edge2_Node000002_Node000003\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M89.5,-99.66C89.5,-90.93 89.5,-80.99 89.5,-74.09\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"86,-99.75 89.5,-109.75 93,-99.75 86,-99.75\"/>\n</a>\n</g>\n</g>\n</g>\n</svg>\n","items":[{"id":"1e8de814-1113-99ac-424d-bf5daf0c06f1","ancestors":[],"type":"function","description":"provides methods for retrieving and manipulating users in a Spring Data JPA repository context.","name":"UserRepository","code":"@Repository\npublic interface UserRepository extends JpaRepository<User, Long> {\n\n  User findByEmail(String email);\n\n  Optional<User> findByUserId(String userId);\n\n  @Query(\"from User user where user.userId = :userId\")\n  @EntityGraph(value = \"User.communities\")\n  Optional<User> findByUserIdWithCommunities(@Param(\"userId\") String userId);\n\n  @Query(\"from User user where user.userId = :userId\")\n  @EntityGraph(value = \"User.userTokens\")\n  Optional<User> findByUserIdWithTokens(@Param(\"userId\") String userId);\n\n  @Query(\"from User user where user.email = :email\")\n  @EntityGraph(value = \"User.userTokens\")\n  Optional<User> findByEmailWithTokens(@Param(\"email\") String email);\n\n  List<User> findAllByCommunities_CommunityId(String communityId, Pageable pageable);\n}","location":{"start":29,"insert":29,"offset":" ","indent":0,"comment":null},"item_type":"interface","length":21}]}}}
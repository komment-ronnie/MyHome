{"name":"Amenity.java","path":"service/src/main/java/com/myhome/domain/Amenity.java","content":{"structured":{"description":"An entity called `Amenity` and its associated domain objects, including a `Community`, `CommunityHouse`, and `AmenityBookingItem`. The `Amenity` class has attributes for `amenityId`, `name`, `description`, and `price`, and it defines relationships with `Community` and `CommunityHouse` using Java's `@ManyToOne` annotation. Additionally, it defines a one-to-many relationship with `AmenityBookingItem` using `@OneToMany`.","image":"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<!-- Generated by graphviz version 2.43.0 (0)\n -->\n<!-- Title: com.myhome.domain.Amenity Pages: 1 -->\n<svg width=\"187pt\" height=\"137pt\"\n viewBox=\"0.00 0.00 187.00 137.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1 1) rotate(0) translate(4 133)\">\n<title>com.myhome.domain.Amenity</title>\n<!-- Node1 -->\n<g id=\"Node000001\" class=\"node\">\n<title>Node1</title>\n<g id=\"a_Node000001\"><a xlink:title=\" \">\n<polygon fill=\"#999999\" stroke=\"#666666\" points=\"173.5,-19 5.5,-19 5.5,0 173.5,0 173.5,-19\"/>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-7\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.domain.Amenity</text>\n</a>\n</g>\n</g>\n<!-- Node2 -->\n<g id=\"Node000002\" class=\"node\">\n<title>Node2</title>\n<g id=\"a_Node000002\"><a xlink:href=\"classcom_1_1myhome_1_1domain_1_1BaseEntity.html\" target=\"_top\" xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"179,-74 0,-74 0,-55 179,-55 179,-74\"/>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-62\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.domain.BaseEntity</text>\n</a>\n</g>\n</g>\n<!-- Node2&#45;&gt;Node1 -->\n<g id=\"edge1_Node000001_Node000002\" class=\"edge\">\n<title>Node2&#45;&gt;Node1</title>\n<g id=\"a_edge1_Node000001_Node000002\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M89.5,-44.66C89.5,-35.93 89.5,-25.99 89.5,-19.09\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"86,-44.75 89.5,-54.75 93,-44.75 86,-44.75\"/>\n</a>\n</g>\n</g>\n<!-- Node3 -->\n<g id=\"Node000003\" class=\"node\">\n<title>Node3</title>\n<g id=\"a_Node000003\"><a xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"126.5,-129 52.5,-129 52.5,-110 126.5,-110 126.5,-129\"/>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-117\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">Serializable</text>\n</a>\n</g>\n</g>\n<!-- Node3&#45;&gt;Node2 -->\n<g id=\"edge2_Node000002_Node000003\" class=\"edge\">\n<title>Node3&#45;&gt;Node2</title>\n<g id=\"a_edge2_Node000002_Node000003\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M89.5,-99.66C89.5,-90.93 89.5,-80.99 89.5,-74.09\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"86,-99.75 89.5,-109.75 93,-99.75 86,-99.75\"/>\n</a>\n</g>\n</g>\n</g>\n</svg>\n","items":[{"id":"37088842-5a1b-febf-4647-3d25f288da0f","ancestors":[],"type":"function","description":"represents an amenity that can be booked by guests at a community or community house, with fields for its unique ID, name, description, price, and relationships to the community and community house.\nFields:\n\t- amenityId (String): represents a unique identifier for an amenity.\n\t- name (String): in the Amenity class represents a string value representing the name of an amenity.\n\t- description (String): in the Amenity class represents a brief summary of the amenity's features or characteristics.\n\t- price (BigDecimal): represents a decimal value that represents the cost of an amenity.\n\t- community (Community): in the Amenity class represents an instance of the Community entity.\n\t- communityHouse (CommunityHouse): represents a reference to a CommunityHouse object within the Amenity entity.\n\t- bookingItems (Set<AmenityBookingItem>): is a set of AmenityBookingItem objects associated with each amenity instance.\n\n","fields":[{"name":"amenityId","type_name":"String","value":null,"constant":false,"class_name":"Amenity","description":"represents a unique identifier for an amenity."},{"name":"name","type_name":"String","value":null,"constant":false,"class_name":"Amenity","description":"in the Amenity class represents a string value representing the name of an amenity."},{"name":"description","type_name":"String","value":null,"constant":false,"class_name":"Amenity","description":"in the Amenity class represents a brief summary of the amenity's features or characteristics."},{"name":"price","type_name":"BigDecimal","value":null,"constant":false,"class_name":"Amenity","description":"represents a decimal value that represents the cost of an amenity."},{"name":"community","type_name":"Community","value":null,"constant":false,"class_name":"Amenity","description":"in the Amenity class represents an instance of the Community entity."},{"name":"communityHouse","type_name":"CommunityHouse","value":null,"constant":false,"class_name":"Amenity","description":"represents a reference to a CommunityHouse object within the Amenity entity."},{"name":"bookingItems","type_name":"Set<AmenityBookingItem>","value":"new HashSet<>()","constant":false,"class_name":"Amenity","description":"is a set of AmenityBookingItem objects associated with each amenity instance."}],"name":"Amenity","code":"@Entity\n@AllArgsConstructor\n@NoArgsConstructor\n@Getter\n@Setter\n@With\n@NamedEntityGraphs({\n    @NamedEntityGraph(\n        name = \"Amenity.community\",\n        attributeNodes = {\n            @NamedAttributeNode(\"community\"),\n        }\n    ),\n    @NamedEntityGraph(\n        name = \"Amenity.bookingItems\",\n        attributeNodes = {\n            @NamedAttributeNode(\"bookingItems\"),\n        }\n    )\n})\n\npublic class Amenity extends BaseEntity {\n  @Column(nullable = false, unique = true)\n  private String amenityId;\n  @Column(nullable = false)\n  private String name;\n  @Column(nullable = false)\n  private String description;\n  @Column(nullable = false)\n  private BigDecimal price;\n  @ManyToOne(fetch = FetchType.LAZY)\n  private Community community;\n  @ManyToOne\n  private CommunityHouse communityHouse;\n  @ToString.Exclude\n  @OneToMany(fetch = FetchType.LAZY, mappedBy = \"amenity\")\n  private Set<AmenityBookingItem> bookingItems = new HashSet<>();\n}","location":{"start":37,"insert":37,"offset":" ","indent":0,"comment":null},"item_type":"class","length":38}]}}}
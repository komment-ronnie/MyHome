{"name":"HouseMemberDocumentService.java","path":"service/src/main/java/com/myhome/services/HouseMemberDocumentService.java","content":{"structured":{"description":"An interface for HouseMemberDocumentService, which handles various operations related to house member documents. The interface includes four methods: deleteHouseMemberDocument, findHouseMemberDocument, updateHouseMemberDocument, and createHouseMemberDocument. These methods allow for the deletion, retrieval, updating, and creation of house member documents, respectively.","image":"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<!-- Generated by graphviz version 2.43.0 (0)\n -->\n<!-- Title: com.myhome.services.HouseMemberDocumentService Pages: 1 -->\n<svg width=\"211pt\" height=\"104pt\"\n viewBox=\"0.00 0.00 211.00 104.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1 1) rotate(0) translate(4 100)\">\n<title>com.myhome.services.HouseMemberDocumentService</title>\n<!-- Node1 -->\n<g id=\"Node000001\" class=\"node\">\n<title>Node1</title>\n<g id=\"a_Node000001\"><a xlink:title=\" \">\n<polygon fill=\"#999999\" stroke=\"#666666\" points=\"182,-96 21,-96 21,-66 182,-66 182,-96\"/>\n<text text-anchor=\"start\" x=\"29\" y=\"-84\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.services.House</text>\n<text text-anchor=\"middle\" x=\"101.5\" y=\"-73\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">MemberDocumentService</text>\n</a>\n</g>\n</g>\n<!-- Node2 -->\n<g id=\"Node000002\" class=\"node\">\n<title>Node2</title>\n<g id=\"a_Node000002\"><a xlink:href=\"classcom_1_1myhome_1_1services_1_1springdatajpa_1_1HouseMemberDocumentSDJpaService.html\" target=\"_top\" xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"203,-30 0,-30 0,0 203,0 203,-30\"/>\n<text text-anchor=\"start\" x=\"8\" y=\"-18\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.services.springdatajpa.</text>\n<text text-anchor=\"middle\" x=\"101.5\" y=\"-7\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">HouseMemberDocumentSDJpaService</text>\n</a>\n</g>\n</g>\n<!-- Node1&#45;&gt;Node2 -->\n<g id=\"edge1_Node000001_Node000002\" class=\"edge\">\n<title>Node1&#45;&gt;Node2</title>\n<g id=\"a_edge1_Node000001_Node000002\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M101.5,-55.54C101.5,-46.96 101.5,-37.61 101.5,-30.16\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"98,-55.8 101.5,-65.8 105,-55.8 98,-55.8\"/>\n</a>\n</g>\n</g>\n</g>\n</svg>\n","items":[{"id":"2f2de200-15fe-2f8a-d64f-1131d0d04395","ancestors":[],"type":"function","description":"provides methods for deleting, finding, updating, and creating House Member Documents.","name":"HouseMemberDocumentService","code":"public interface HouseMemberDocumentService {\n\n  boolean deleteHouseMemberDocument(String memberId);\n\n  Optional<HouseMemberDocument> findHouseMemberDocument(String memberId);\n\n  Optional<HouseMemberDocument> updateHouseMemberDocument(MultipartFile multipartFile,\n      String memberId);\n\n  Optional<HouseMemberDocument> createHouseMemberDocument(MultipartFile multipartFile,\n      String memberId);\n}","location":{"start":23,"insert":23,"offset":" ","indent":0,"comment":null},"item_type":"interface","length":12}]}}}
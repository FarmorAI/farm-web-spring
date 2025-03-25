package com.farmorai.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;

import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfoService {
    /**
     * 기본적으로 XML을 파싱할 때, 외부 엔터티를 허용하므로 XXE 공격을 방지하는 코드 추가
     */
    private DocumentBuilderFactory secureDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);   // DOCTYPE 선언을 비활성화
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);   // 외부 엔터티 사용을 금지
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);   // 외부 파라미터 엔터티 사용을 금지
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);  // 외부 DTD 로딩을 금지
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        factory.setExpandEntityReferences(false);
        return factory;
    }


    @Value("${KAKAO_API_KEY}")
    private String apiKey;

    @Value("${NONGSARO_API_KEY}")
    private String nongsaroApiKey;

    private final RestTemplate restTemplate;

    public InfoService() {
        this.restTemplate = new RestTemplate();
    }

    // ✅ 카카오 블로그 정보 가져오기
    public ResponseEntity<Map<String, Object>> getBlogInfo(String query) {
        try {
            String url = "https://dapi.kakao.com/v2/search/blog?query=" + query + "&size=300&page=1&sort=accuracy";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + apiKey);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            List<Map<String, Object>> filteredResults = ((List<Map<String, Object>>) response.getBody().get("documents"))
                    .stream()
                    .map(this::removeHtmlTags)  // HTML 태그 제거
                    .filter(post -> post.get("thumbnail") != null && !((String) post.get("thumbnail")).isEmpty()) // 썸네일 없는 것 제외
                    .collect(Collectors.toList());

            return new ResponseEntity<>(Map.of("documents", filteredResults), response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ 농사로 기술 정보 가져오기 - 농업기술
    public ResponseEntity<List<Map<String, String>>> getTechInfo(String query) {
        try {
/*            String url = UriComponentsBuilder.fromHttpUrl("http://api.nongsaro.go.kr/service/monthFarmTech/monthFarmTechLst")
                    .queryParam("apiKey", nongsaroApiKey) // API 키 추가
                    .queryParam("srchStr", query)
                    .encode()
                    .toUriString();*/
            String url = "http://api.nongsaro.go.kr/service/monthFarmTech/monthFarmTechLst?apiKey=" + nongsaroApiKey + "&srchStr=사과";
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML)); // XML 응답 요청

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getBody() == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<Map<String, String>> extractedData = parseTechXml(response.getBody(), "item");

            // 추가 요청을 통해 atchmnflUrl 가져오기
            extractedData.forEach(item -> {
                String contentId = item.get("contentId");
                if (contentId != null && !contentId.isEmpty()) {
                    String atchmnflUrl = fetchTechAtchmnflUrl(contentId);
                    item.put("url", atchmnflUrl);
                }
            });
            return new ResponseEntity<>(extractedData, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // ✅ 특정 콘텐츠 ID로 농업기술 링크 가져오기
    private String fetchTechAtchmnflUrl(String contentId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://api.nongsaro.go.kr/service/monthFarmTech/monthFarmTechDtlDefaultInfo")
                    .queryParam("apiKey", nongsaroApiKey)
                    .queryParam("srchCurationNo", contentId)
                    .encode()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getBody() == null) {
                return "";
            }

            return parseAtchmnflUrl(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    // ✅ XML에서 atchmnflUrl 추출
    private String parseAtchmnflUrl(String xmlString){
        try {
            DocumentBuilderFactory factory = secureDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            document.getDocumentElement().normalize();
            return getTagValue("atchmnflUrl", document.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    // ✅ 농사로 병해충 정보 가져오기
    public ResponseEntity<List<Map<String, String>>> getBugInfo() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://api.nongsaro.go.kr/service/dbyhsCccrrncInfo/dbyhsCccrrncInfoList")
                    .queryParam("apiKey", nongsaroApiKey) // API 키 추가
                    .encode()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getBody() == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<Map<String, String>> extractedData = parseBugXml(response.getBody(), "item");

            return new ResponseEntity<>(extractedData, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ✅ XML을 직접 파싱하여 필요한 정보만 추출하는 함수
    private List<Map<String, String>> parseTechXml(String xmlString, String tagName) {
        List<Map<String, String>> resultList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = secureDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            document.getDocumentElement().normalize(); // XML 정규화

            // ✅ 태그명에 해당하는 데이터만 추출
            NodeList items = document.getElementsByTagName(tagName);

            for (int i = 0; i < items.getLength(); i++) {
                Node node = items.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Map<String, String> itemMap = new HashMap<>();

                    itemMap.put("thumbnailUrl", getTagValue("curationImgUrl", element)); // 썸네일 URL
                    itemMap.put("contentId", getTagValue("curationNo", element)); // 콘텐츠 고유번호
                    itemMap.put("title", getTagValue("curationNm", element)); // 제목


                    resultList.add(itemMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
    private List<Map<String, String>> parseBugXml(String xmlString, String tagName) {
        List<Map<String, String>> resultList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = secureDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            document.getDocumentElement().normalize(); // XML 정규화

            // ✅ 태그명에 해당하는 데이터만 추출
            NodeList items = document.getElementsByTagName(tagName);

            for (int i = 0; i < items.getLength(); i++) {
                Node node = items.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Map<String, String> itemMap = new HashMap<>();

                    itemMap.put("title", getTagValue("cntntsSj", element)); // 컨텐츠 제목
                    itemMap.put("fileurl", getTagValue("downFile", element)); // 파일 경로


                    resultList.add(itemMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    // ✅ 특정 XML 태그 값을 가져오는 함수
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent().trim();
        }
        return "";
    }

    private Map<String, Object> removeHtmlTags(Map<String, Object> post) {
        Pattern htmlPattern = Pattern.compile("<[^>]*>");
        Map<String, Object> cleanPost = new HashMap<>(post);

        cleanPost.put("title", htmlPattern.matcher((String) post.get("title")).replaceAll(""));
        cleanPost.put("contents", htmlPattern.matcher((String) post.get("contents")).replaceAll(""));

        return cleanPost;
    }
}
package controller.ohsung;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MimeDecoder {

    private static MimeDecoder instance;

    private MimeDecoder() {}

    public static MimeDecoder getInstance() {
        if (instance == null) {
            instance = new MimeDecoder();
        }
        return instance;
    }

    public String decodeMimeText(String text) {
        try {
            // =?UTF-8?B?...?= 형식의 인코딩된 텍스트를 찾아 디코딩
            Pattern pattern = Pattern.compile("=\\?([^?]+)\\?([BbQq])\\?([^?]+)\\?=");
            Matcher matcher = pattern.matcher(text);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String charset = matcher.group(1);
                String encoding = matcher.group(2).toUpperCase();
                String encodedText = matcher.group(3);

                String decodedText;
                if (encoding.equals("B")) {
                    // Base64 디코딩
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
                    decodedText = new String(decodedBytes, charset);
                } else {
                    // Quoted-Printable은 현재 단순 대체
                    decodedText = encodedText;
                }

                matcher.appendReplacement(result, Matcher.quoteReplacement(decodedText));
            }
            matcher.appendTail(result);

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }
    }

    public String extractBodyFromFetch(String fetchLine) {
        Pattern pattern = Pattern.compile("\\{\\d+\\}");
        Matcher matcher = pattern.matcher(fetchLine);
        if (matcher.find()) {
            int startIndex = matcher.end();
            if (startIndex < fetchLine.length()) {
                return fetchLine.substring(startIndex);
            }
        }
        return "";
    }

    public String parseEmailBody(String body) {
        try {
            // Content-Type 헤더에서 boundary 찾기
            Pattern boundaryPattern = Pattern.compile("boundary=\"?([^\"\r\n]+)\"?");
            Matcher boundaryMatcher = boundaryPattern.matcher(body);

            if (boundaryMatcher.find()) {
                String boundary = boundaryMatcher.group(1);
                // 각 파트를 분리
                String[] parts = body.split("--" + boundary);

                for (String part : parts) {
                    // text/plain 파트 찾기
                    if (part.contains("Content-Type: text/plain")) {
                        // charset 찾기
                        Pattern charsetPattern = Pattern.compile("charset=\"?([^\"\\s;]+)\"?");
                        Matcher charsetMatcher = charsetPattern.matcher(part);
                        String charset = charsetMatcher.find() ? charsetMatcher.group(1) : "UTF-8";

                        // Content-Transfer-Encoding 확인
                        if (part.contains("Content-Transfer-Encoding: base64")) {
                            // 헤더와 본문 구분자 찾기
                            String[] headerAndBody = part.split("\r?\n\r?\n", 2);
                            if (headerAndBody.length > 1) {
                                String base64Content = headerAndBody[1].replaceAll("\\s+", "");
                                try {
                                    byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
                                    String decodedText = new String(decodedBytes, charset);

                                    // HTML 태그가 포함되어 있는지 확인
                                    if (containsHtml(decodedText)) {
                                        return "SWING에서 구현하기 복잡한 HTML태그입니다.";
                                    }

                                    return decodedText;
                                } catch (IllegalArgumentException e) {
                                    return "Base64 디코딩 실패: " + e.getMessage();
                                }
                            }
                        } else {
                            // Base64가 아닌 경우, 헤더 이후의 내용 반환
                            String[] headerAndBody = part.split("\r?\n\r?\n", 2);
                            if (headerAndBody.length > 1) {
                                String decodedText = headerAndBody[1].trim();

                                // HTML 태그가 포함되어 있는지 확인
                                if (containsHtml(decodedText)) {
                                    return "SWING에서 구현하기 복잡한 HTML태그입니다.";
                                }

                                return decodedText;
                            }
                        }
                    }
                }
            }

            // multipart가 아닌 경우 기존 방식으로 처리
            Pattern charsetPattern = Pattern.compile("charset=([^\\s;]+)");
            Matcher charsetMatcher = charsetPattern.matcher(body);
            String charset = charsetMatcher.find() ? charsetMatcher.group(1).replace("\"", "") : "UTF-8";

            if (body.contains("Content-Transfer-Encoding: base64")) {
                Pattern base64Pattern = Pattern.compile(
                        "Content-Transfer-Encoding: base64\\r?\\n\\r?\\n([^\\r\\n]+)",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                );
                Matcher base64Matcher = base64Pattern.matcher(body);

                if (base64Matcher.find()) {
                    String base64Content = base64Matcher.group(1).replaceAll("\\s+", "");
                    try {
                        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
                        String decodedText = new String(decodedBytes, charset);

                        // HTML 태그가 포함되어 있는지 확인
                        if (containsHtml(decodedText)) {
                            return body + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "SWING에서 구현하기 복잡한 HTML태그입니다.\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
                        }

                        return decodedText;
                    } catch (IllegalArgumentException e) {
                        return "Base64 디코딩 실패: " + e.getMessage();
                    }
                }
            }

            // HTML 태그가 포함되어 있는지 확인
            if (containsHtml(body)) {
                return body + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "SWING에서 구현하기 복잡한 HTML태그입니다.\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" ;
            }

            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return "메일 내용을 디코딩하는 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // HTML 태그 포함 여부를 확인하는 헬퍼 메서드
    private boolean containsHtml(String text) {
        Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
        return htmlPattern.matcher(text).matches();
    }

}
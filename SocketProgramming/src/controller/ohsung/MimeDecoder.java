package controller.ohsung;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MimeDecoder {

    // MIME 인코딩 패턴을 정의하는 정규 표현식
    // 인코딩 방식은 B(Base 64)와 Q(Quoted-Printable)방식
    private static final Pattern MIME_PATTERN = Pattern.compile("=\\?([^?]+)\\?([BQ])\\?([^?]+)\\?=", Pattern.CASE_INSENSITIVE);

    //싱글톤을 위한 인스턴스 생성
    private static final MimeDecoder INSTANCE = new MimeDecoder();

    private MimeDecoder(){

    }

    //싱글톤 인스턴스 반환 메서드
    public static MimeDecoder getInstance(){
        return INSTANCE;
    }

    /**
     * MIME 인코딩된 텍스트를 디코딩하는 메서드
     * @param encodedText MIME 형식으로 인코딩된 텍스트
     * @return 디코딩된 텍스트
     */
    public String decodeMimeText(String encodedText){

        //정규표현식을 통해 MIME 패턴에 맞는 부분을 찾음
        Matcher matcher = MIME_PATTERN.matcher(encodedText);
        StringBuilder decodedText = new StringBuilder();

        int lastEnd = 0;
        while (matcher.find()) {
            // 미처리 텍스트(인코딩 되지 않은 부분) 추가
            decodedText.append(encodedText, lastEnd, matcher.start());

            String charset = matcher.group(1); //텍스트의 문자 집합 정보
            String encodingType = matcher.group(2).toUpperCase(); // 인코딩 방식 (B 또는 Q)
            String encodedContent = matcher.group(3); //인코딩된 실제 텍스트

            String decodedSegment;
            try {
                decodedSegment = decodeSegment(encodedContent, encodingType, charset); //인코딩된 텍스트 디코딩
            } catch (Exception e) {
                decodedSegment = encodedContent; //디코딩 실패 시 원본 사용
            }

            decodedText.append(decodedSegment); //디코딩한 텍스트를 결과에 추가
            lastEnd = matcher.end(); // 현재 위치 갱신
        }

        //마지막으로 남은 미처리 텍스트 추가
        decodedText.append(encodedText.substring(lastEnd));
        return decodedText.toString(); //디코딩 완료된 텍스트 반환
    }

    /**
     * HTML 태그를 제거하여 본문에서 순수 텍스트만 반환하는 메서드
     * @param htmlText HTML 태그가 포함된 텍스트
     * @return HTML 태그가 제거된 순수 텍스트
     */
    private String removeHtmlTags(String htmlText){
        return htmlText.replaceAll("<[^>]+>", " ").replaceAll("&nbsp;", " ");
    }

    /**
     * 인코딩된 본문에서 순수 텍스트를 추출하는 메서드
     * @param input MIME 형식의 입력 텍스트
     * @return 순수 텍스트(디코딩 및 HTML 태그 제거)
     */
    public String extractPlainText(String input){
        String mimeDecodedText = decodeMimeText(input);

        return removeHtmlTags(mimeDecodedText).trim();
    }

    /**
     * 주어진 인코딩 방식과 문자 집합을 사용하여 텍스트를 디코딩하는 메서드
     * @param encodedContent 인코딩된 텍스트
     * @param encodingType 인코딩 방식 (B 또는 Q)
     * @param charsetName 문자 집합 이름
     * @return 디코딩된 텍스트
     * @throws Exception 문자 집합이 지원되지 않는 경우 예외 발생
     */
    private String decodeSegment(String encodedContent, String encodingType, String charsetName) throws Exception{
        byte[] decodedBytes;

        if (encodingType.equals("B")) { // Base64 인코딩 처리
            decodedBytes = Base64.getDecoder().decode(encodedContent);
        } else if (encodingType.equals("Q")) { // Quoted-Printable 인코딩 처리
            decodedBytes = decodeQuotedPrintable(encodedContent);
        } else {
            return encodedContent; //지원되지 않는 타입이면 원본 반환
        }

        //지정된 문자 집합으로 바이트 배열을 문자열로 변환하여 반환
        Charset charset = Charset.forName(charsetName);
        return new String(decodedBytes, charset);
    }

    /**
     * Quoted-Printable 형식의 텍스트를 디코딩하는 메서드
     * @param encodedContent 인코딩된 텍스트
     * @return 디코딩된 바이트 배열
     */
    private byte[] decodeQuotedPrintable(String encodedContent) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        for (int i = 0; i < encodedContent.length(); i++) {
            char c = encodedContent.charAt(i);

            if (c == '=') {  // '=' 기호는 다음 두 문자가 Hex로 인코딩된 경우
                if (i + 2 < encodedContent.length()) {
                    int high = Character.digit(encodedContent.charAt(i + 1), 16);
                    int low = Character.digit(encodedContent.charAt(i + 2), 16);
                    if (high == -1 || low == -1) continue;  // 잘못된 인코딩일 경우 건너뜀

                    output.write((high << 4) + low);  // Hex 값을 바이트로 변환
                    i += 2;  // 다음 문자 두 개를 이미 처리했으므로 인덱스 증가
                }
            } else {
                output.write(c);  // 인코딩되지 않은 문자는 그대로 추가
            }
        }
        return output.toByteArray();
    }


}

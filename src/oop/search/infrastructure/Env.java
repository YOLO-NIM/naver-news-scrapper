package oop.search.infrastructure;

/// 환경변수를 안전하게 읽기 위한 유틸 클래스
public final class Env {
    private Env(){

    }
    public static String getRequired(String key){
        String value = System.getenv(key);

        if(value == null || value.isBlank()){
            throw new IllegalStateException(
                    key + " 환경변수가 설정되지 않았습니다."
            );
        }
        return value;
    }

    public static int getRequiredInt(String key){
        String value = System.getenv(key);

        try {
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            throw new IllegalStateException(
                    key + " 환경변수는 숫자여야 합니다. 현재 값 : " + value
            );
        }
    }

    public static <T extends Enum<T>> T getRequiredEnum(String key, Class<T> enumType){
        String value = getRequired(key).toUpperCase();

        try {
            return Enum.valueOf(enumType, value);
        }catch (IllegalArgumentException e){
            throw new IllegalStateException(
                    key + " 환경변수 값이 올바르지 않습니다. 현재 값: " + value
            );
        }
    }
}

package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

public enum ParamType {
    String("string"),
    Boolean("boolean"),
    Character("character"),
    Byte("byte"),
    Short("short"),
    Integer("integer"),
    Long("long"),
    Float("float"),
    Double("double"),
    Void("void");

    private String paramType;

    ParamType(java.lang.String paramType) {
        this.paramType = paramType;
    }

    public java.lang.String getParamType() {
        return paramType;
    }

    public void setParamType(java.lang.String paramType) {
        this.paramType = paramType;
    }

    /**
     * 前端传入的类型有可能是全限定名或简称，所以枚举中列举最简类型
     * @param key 入参类型
     * @return
     */
    public static boolean containsKey(String key){
        for (ParamType paramType : ParamType.values()) {
            if (key.toLowerCase().contains(paramType.paramType)){
                return true;
            }
        }
        return false;
    }

}
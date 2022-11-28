package cn.annacode.org.common.tool.tool;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class PinYinUtil {
    public static String getPinyin(String china){
        HanyuPinyinOutputFormat formart= new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] arrays = china.trim().toCharArray();
        String result = "";
        try{
            for (char i : arrays) {
                char ti =i;
//                String re =new Regex("[\\u4e00-\\u9fa5]");
                if(Character.toString(ti).matches("[\\u4e00-\\u9fa5]")){
                    String[] temp =PinyinHelper.toHanyuPinyinStringArray(ti,formart);
                    result +=temp[0];
                }else {
                    result += ti;
                }
                result+="_";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}

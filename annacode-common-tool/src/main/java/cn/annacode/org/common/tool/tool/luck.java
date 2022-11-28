package cn.annacode.org.common.tool.tool;


import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.*;

public class luck {
    /**
     * luck
     * @param drawChances 奖品概率数组
     * @return 奖品编号
     */
    public static Integer drawId(int[] drawChances){
        Random r = new Random();
        TreeMap<Double,Integer> map = new TreeMap<>();
        double max =  0.0;
        for (int i = 0; i < drawChances.length; i++) {
            double d = r.nextDouble();
            double m = d * drawChances[i];
            if (max < m)
                max = m;
            map.put(d*drawChances[i],i);
        }
        if (map.size() == drawChances.length)
            return map.get(map.lastKey());;
        return drawId(drawChances);
    }

    /**
     * 可返回抽到的key
     * @param drawChanceMap 抽奖map key奖品 value 概率
     * @return key
     */
    public static Object draw(Map<Object,Integer> drawChanceMap){
        Object[] names = drawChanceMap.keySet().toArray();
        Object[] values = drawChanceMap.values().toArray();
        int[] v = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            v[i] = (int) Math.round((Double) values[i]);
        }
        return names[drawId(v)];
    }
    @Test
    public void test(){
        int[] a = new int[]{2,3,100};
        int[] r = new int[]{0,0,0};
        for (int i = 0 ; i < 1000 ; i++){
            r[drawId(a)]++;
        }
        System.out.println(Arrays.toString(r));
        HashMap map = new HashMap() {
            {
                put("书奶奶",10);
                put("爱你",30);
                put("天国",4);
                put("y",77);
                put("r",88);
                put("e",99);
                put("g",44);
            }
        };


        HashMap<Object,Integer> map2 = new HashMap<>() {
            {
                put("书奶奶",0);
                put("爱你",0);
                put("天国",0);
                put("y",0);
                put("r",0);
                put("e",0);
                put("g",0);
            }
        };
        HashMap mapBoolean = new HashMap() {
            {
                put(true,10);
                put(false,30);
            }
        };
        Gson gson = new Gson();
        String j = "{true:3,false:7}";
        mapBoolean = gson.fromJson(j,HashMap.class);
        System.out.println(mapBoolean);
        System.out.println(mapBoolean.keySet().toArray()[0].getClass());
        HashMap<Object,Integer> mapBoolea2 = new HashMap() {
            {
                put(true,0);
                put(false,0);
            }
        };
        for (int i = 0; i < 10000; i++) {
            Object o = draw(mapBoolean);
            mapBoolea2.put(o,mapBoolea2.get(o)+1);
        }
        System.out.println(mapBoolea2);
    }
}


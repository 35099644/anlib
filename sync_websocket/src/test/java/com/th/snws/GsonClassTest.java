package com.th.snws;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by work on 12/07/2017.
 */

@RunWith(JUnit4.class)
public class GsonClassTest {

    public static class Bean {
        Class clazz;
        int i;

        @Override
        public String toString() {
            return "Bean{" +
                    "clazz=" + clazz +
                    ", i=" + i +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bean bean = (Bean) o;

            if (i != bean.i) return false;
            return clazz != null ? clazz.equals(bean.clazz) : bean.clazz == null;

        }

        @Override
        public int hashCode() {
            int result = clazz != null ? clazz.hashCode() : 0;
            result = 31 * result + i;
            return result;
        }
    }

    @Test
    public void test() {
        Bean bean = new Bean();
        bean.i = 3;
        bean.clazz = String.class;

        String json = new Gson().toJson(bean);
        Bean bean2 = new Gson().fromJson(json, Bean.class);

        Assert.assertEquals(bean, bean2);
    }
}

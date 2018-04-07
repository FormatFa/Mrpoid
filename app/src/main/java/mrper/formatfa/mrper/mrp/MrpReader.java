package mrper.formatfa.mrper.mrp;

import java.io.FileInputStream;
import java.io.InputStream;

public class MrpReader {
    String name="读取失败";
    String info="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static MrpReader readFile(String path)
    {
        MrpReader reader = new MrpReader();
        try {
            InputStream is = new FileInputStream(path);
            byte[] buff = new byte[24];
            is.skip(28);
            is.read(buff);
            reader.setName(new String(buff,"GB2312"));
            is.skip(76);

            buff = new byte[64];
            is.read(buff);
            reader.setInfo(new String(buff,"GB2312"));
            is.close();
        }
        catch (Exception exection)
        {

        }
        return reader;
    }
}

package mrper.formatfa.mrper.mrp;

public class MrpItem {
   private String id;
    private String filename;
    private  String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDonwload() {
        return donwload;
    }

    public void setDonwload(String donwload) {
        this.donwload = donwload;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private  String size;
    private String donwload;
    private String info;
}

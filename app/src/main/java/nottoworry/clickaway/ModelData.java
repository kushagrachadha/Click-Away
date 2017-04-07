package nottoworry.clickaway;

/**
 * Created by sahil on 11/2/17.
 */

public class ModelData {

    private String name, address, phoneNo;
    private static int id;

    public ModelData(String n, String a, String p){
        this.name =n;
        this.address = a;
        this.phoneNo = p;
        id++;
    }

    public int getId(){return id;}

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }
}

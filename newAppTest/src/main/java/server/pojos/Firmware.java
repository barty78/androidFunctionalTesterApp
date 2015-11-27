
package server.pojos;

import com.google.gson.annotations.Expose;

public class Firmware {

    @Expose
    private long id;
    @Expose
    private String version;
    @Expose
    private String model;
    @Expose
    private String md5sum;
    @Expose
    private String url;

    /**
     * 
     * @return
     *     The id
     */
    public long getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The version
     */
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     *     The version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * @return
     *     The model
     */
    public String getModel() {
        return model;
    }

    /**
     * 
     * @param model
     *     The model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 
     * @return
     *     The md5sum
     */
    public String getMd5sum() {
        return md5sum;
    }

    /**
     * 
     * @param md5sum
     *     The md5sum
     */
    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}

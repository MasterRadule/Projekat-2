package ktsnvt.tim1.DTOs;

public class MediaFileDTO {

    private Long id;
    private String fileType;
    private String dataBase64;

    public MediaFileDTO(Long id, String fileType, String dataBase64) {
        this.id = id;
        this.fileType = fileType;
        this.dataBase64 = dataBase64;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDataBase64() {
        return dataBase64;
    }

    public void setDataBase64(String dataBase64) {
        this.dataBase64 = dataBase64;
    }
}

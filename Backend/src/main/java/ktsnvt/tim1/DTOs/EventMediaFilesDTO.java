package ktsnvt.tim1.DTOs;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class EventMediaFilesDTO {

    private Long eventID;

    @NotNull
    private MultipartFile[] files;

    public EventMediaFilesDTO() {
    }

    public EventMediaFilesDTO(Long eventID, @NotNull MultipartFile[] files) {
        this.eventID = eventID;
        this.files = files;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long id) {
        this.eventID = id;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }
}

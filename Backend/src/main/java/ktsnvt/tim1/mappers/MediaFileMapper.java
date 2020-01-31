package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.MediaFileDTO;
import ktsnvt.tim1.model.MediaFile;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class MediaFileMapper implements IMapper<MediaFile, MediaFileDTO> {

    @Override
    public MediaFile toEntity(MediaFileDTO dto) {
        return null;
    }

    @Override
    public MediaFileDTO toDTO(MediaFile mediaFile) {
        String dataBase64 = Base64.getEncoder().encodeToString(mediaFile.getData());
        String type = mediaFile.getFileType().startsWith("image") ? "image" : "video";
        return new MediaFileDTO(mediaFile.getId(), type, dataBase64);
    }
}

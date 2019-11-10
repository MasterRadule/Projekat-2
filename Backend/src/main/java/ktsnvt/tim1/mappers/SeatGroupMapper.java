package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.SeatGroupDTO;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.SeatGroup;
import org.springframework.stereotype.Component;

@Component
public class SeatGroupMapper implements IMapper<SeatGroup, SeatGroupDTO> {
    @Override
    public SeatGroup toEntity(SeatGroupDTO dto) throws EntityNotValidException {
        SeatGroup seatGroup = new SeatGroup();

        seatGroup.setId(null);
        seatGroup.setParterre(dto.isParterre());
        seatGroup.setxCoordinate(dto.getxCoordinate());
        seatGroup.setyCoordinate(dto.getyCoordinate());

        if (dto.isParterre()) {
            seatGroup.setColsNum(null);
            seatGroup.setRowsNum(null);
            if (dto.getTotalSeats() == null || dto.getTotalSeats() <= 0) {
                throw new EntityNotValidException("Invalid value for parterre's total seats.");
            }
            seatGroup.setTotalSeats(dto.getTotalSeats());
        } else if (dto.getColsNum() == null || dto.getColsNum() < 1 || dto.getRowsNum() == null || dto
                .getRowsNum() < 1) {
            throw new EntityNotValidException("Row and column numbers must be specified");
        } else {
            seatGroup.setRowsNum(dto.getRowsNum());
            seatGroup.setColsNum(dto.getColsNum());
            seatGroup.setTotalSeats(dto.getRowsNum() * dto.getColsNum());
        }

        return seatGroup;
    }

    @Override
    public SeatGroupDTO toDTO(SeatGroup seatGroup) {
        SeatGroupDTO dto = new SeatGroupDTO();
        dto.setId(seatGroup.getId());
        dto.setColsNum(seatGroup.getColsNum());
        dto.setRowsNum(seatGroup.getRowsNum());
        dto.setParterre(seatGroup.getParterre());
        dto.setxCoordinate(seatGroup.getxCoordinate());
        dto.setyCoordinate(seatGroup.getyCoordinate());
        dto.setTotalSeats(seatGroup.getTotalSeats());

        return dto;
    }
}

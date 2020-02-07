package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.SeatDTO;
import ktsnvt.tim1.model.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper implements IMapper<Seat, SeatDTO> {

    @Override
    public Seat toEntity(SeatDTO dto) {
        return null;
    }

    @Override
    public SeatDTO toDTO(Seat seat) {
        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setId(seat.getId());
        seatDTO.setRowNum(seat.getRowNum());
        seatDTO.setColNum(seat.getColNum());
        seatDTO.setReserved(seat.getTicket() != null);

        return seatDTO;
    }
}

package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.ReservableSeatGroupDTO;
import ktsnvt.tim1.model.ReservableSeatGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ReservableSeatGroupMapper implements IMapper<ReservableSeatGroup, ReservableSeatGroupDTO> {

    @Autowired
    SeatMapper seatMapper;

    @Override
    public ReservableSeatGroup toEntity(ReservableSeatGroupDTO dto) {
        return null;
    }

    @Override
    public ReservableSeatGroupDTO toDTO(ReservableSeatGroup reservableSeatGroup) {
        ReservableSeatGroupDTO rsgDTO = new ReservableSeatGroupDTO();
        rsgDTO.setId(reservableSeatGroup.getId());
        rsgDTO.setEsgID(reservableSeatGroup.getEventSeatGroup().getId());
        rsgDTO.setEventDayID(reservableSeatGroup.getEventDay().getId());
        rsgDTO.setFreeSeats(reservableSeatGroup.getFreeSeats());
        rsgDTO.setSeats(reservableSeatGroup.getSeats()
                .stream().map(s -> seatMapper.toDTO(s)).collect(Collectors.toSet()));

        return rsgDTO;
    }
}

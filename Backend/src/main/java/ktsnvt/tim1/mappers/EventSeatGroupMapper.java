package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.EventSeatGroupDTO;
import ktsnvt.tim1.model.EventSeatGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventSeatGroupMapper implements IMapper<EventSeatGroup, EventSeatGroupDTO> {

    @Autowired
    private ReservableSeatGroupMapper reservableSeatGroupMapper;

    @Override
    public EventSeatGroup toEntity(EventSeatGroupDTO dto) {
        return null;
    }

    @Override
    public EventSeatGroupDTO toDTO(EventSeatGroup eventSeatGroup) {
        EventSeatGroupDTO esgDTO = new EventSeatGroupDTO();
        esgDTO.setSeatGroupID(eventSeatGroup.getSeatGroup().getId());
        esgDTO.setPrice(eventSeatGroup.getPrice());
        esgDTO.setReservableSeatGroups(eventSeatGroup.getReservableSeatGroups()
                .stream().map(rsg -> reservableSeatGroupMapper.toDTO(rsg)).collect(Collectors.toSet()));

        return esgDTO;
    }
}

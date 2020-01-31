package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.EventSeatGroupDTO;
import ktsnvt.tim1.model.EventSeatGroup;
import org.springframework.stereotype.Component;

@Component
public class EventSeatGroupMapper implements IMapper<EventSeatGroup, EventSeatGroupDTO> {
    @Override
    public EventSeatGroup toEntity(EventSeatGroupDTO dto) {
        return null;
    }

    @Override
    public EventSeatGroupDTO toDTO(EventSeatGroup eventSeatGroup) {
        EventSeatGroupDTO esgDTO = new EventSeatGroupDTO();
        esgDTO.setSeatGroupID(eventSeatGroup.getSeatGroup().getId());
        esgDTO.setPrice(eventSeatGroup.getPrice());

        return esgDTO;
    }
}

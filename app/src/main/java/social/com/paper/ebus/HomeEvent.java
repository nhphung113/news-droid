package social.com.paper.ebus;

import social.com.paper.dto.PaperDto;

/**
 * Created by phung on 7/17/2016.
 */
public class HomeEvent {

    public PaperDto paperDto;
    public int positionCate;

    public HomeEvent() {}

    public HomeEvent(int positionCate, PaperDto paperDto) {
        this.positionCate = positionCate;
        this.paperDto = paperDto;
    }
}

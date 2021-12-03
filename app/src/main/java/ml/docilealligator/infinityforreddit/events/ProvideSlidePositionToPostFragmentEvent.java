package ml.docilealligator.infinityforreddit.events;

public class ProvideSlidePositionToPostFragmentEvent {
    public final long postFragmentTimeId;
    public final int postSlidePosition;

    public ProvideSlidePositionToPostFragmentEvent(long postFragmentTimeId, int postSlidePosition) {
        this.postFragmentTimeId = postFragmentTimeId;
        this.postSlidePosition = postSlidePosition;
    }
}

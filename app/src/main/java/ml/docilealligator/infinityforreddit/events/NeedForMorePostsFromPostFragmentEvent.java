package ml.docilealligator.infinityforreddit.events;

public class NeedForMorePostsFromPostFragmentEvent {
    public final long postFragmentTimeId;

    public NeedForMorePostsFromPostFragmentEvent(long postFragmentTimeId) {
        this.postFragmentTimeId = postFragmentTimeId;
    }
}

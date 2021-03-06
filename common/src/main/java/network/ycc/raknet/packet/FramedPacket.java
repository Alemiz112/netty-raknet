package network.ycc.raknet.packet;

public interface FramedPacket extends Packet {

    Reliability getReliability();

    void setReliability(Reliability reliability);

    int getOrderChannel();

    void setOrderChannel(int orderChannel);

    enum Reliability {
        //						  REL    ORD    SEQ    ACK
        UNRELIABLE(               false, false, false, false),
        UNRELIABLE_SEQUENCED(     false, true,  true,  false),
        RELIABLE(                 true,  false, false, false),
        RELIABLE_ORDERED(         true,  true,  false, false),
        RELIABLE_SEQUENCED(       true,  true,  true,  false),
        UNRELIABLE_ACK(           false, false, false, true),
        RELIABLE_ACK(             true,  false, false, true),
        RELIABLE_ORDERED_ACK(     true,  true,  false, true);

        public final boolean isReliable;
        public final boolean isOrdered;
        public final boolean isSequenced;
        public final boolean isAckd;

        Reliability(boolean isReliable, boolean isOrdered, boolean isSequenced, boolean isAckd) {
            this.isReliable = isReliable;
            this.isOrdered = isOrdered;
            this.isSequenced = isSequenced;
            this.isAckd = isAckd;
        }

        public static Reliability get(int code) {
            assert code >= 0 && code < values().length;
            return values()[code];
        }

        public int code() {
            return ordinal();
        }

        public Reliability makeReliable() {
            if (isReliable) {
                return this;
            }
            switch (this) {
                case UNRELIABLE:
                    return RELIABLE;
                case UNRELIABLE_SEQUENCED:
                    return RELIABLE_SEQUENCED;
                case UNRELIABLE_ACK:
                    return RELIABLE_ACK;
                default:
                    throw new IllegalArgumentException("No reliable form of " + this);
            }
        }
    }

}

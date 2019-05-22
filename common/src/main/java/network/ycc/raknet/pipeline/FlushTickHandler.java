package network.ycc.raknet.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

/**
 * This handler produces an automatic flush cycle that is driven
 * by the channel IO itself. The ping produced in
 * {@link AbstractConnectionInitializer} serves as a timed
 * driver if no IO is present. The channel write signal is driven by
 * {@link ReliabilityHandler} using {@link FlushTickHandler#checkFlushTick(Channel)}.
 */
public class FlushTickHandler extends ChannelDuplexHandler {

    public static final String NAME = "rn-flush-tick";
    public static final long TICK_RESOLUTION = TimeUnit.NANOSECONDS.convert(5, TimeUnit.MILLISECONDS);

    public static void checkFlushTick(Channel channel) {
        channel.pipeline().fireUserEventTriggered(FLUSH_CHECK_SIGNAL);
    }

    protected static final Object FLUSH_CHECK_SIGNAL = new Object();

    //TODO: keep a channel attr that stores a long # of ticks?
    protected long tickAccum = 0;
    protected long lastTickAccum = System.nanoTime();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        maybeFlush(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == FLUSH_CHECK_SIGNAL) {
            maybeFlush(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (tickAccum >= TICK_RESOLUTION) {
            tickAccum -= TICK_RESOLUTION;
        } else {
            tickAccum = 0;
        }
        super.flush(ctx);
    }

    protected void maybeFlush(Channel channel) {
        if (channel == null) {
            return;
        }
        final long curTime = System.nanoTime();
        tickAccum += curTime - lastTickAccum;
        lastTickAccum = curTime;
        if (tickAccum >= TICK_RESOLUTION) {
            channel.flush();
        }
    }

}

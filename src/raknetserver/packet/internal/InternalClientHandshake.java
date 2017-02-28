package raknetserver.packet.internal;

import io.netty.buffer.ByteBuf;
import raknetserver.packet.RakNetDataSerializer;

public class InternalClientHandshake implements InternalPacket {

	@Override
	public void decode(ByteBuf buf) {
		for (int i = 0; i < 11; i++) {
			RakNetDataSerializer.readAddress(buf);
		}
		buf.skipBytes(8); //pong time
		buf.skipBytes(8); //timestamp
		buf.skipBytes(70); //WTF is this?
	}

	@Override
	public void encode(ByteBuf buf) {
		throw new UnsupportedOperationException();
	}

}

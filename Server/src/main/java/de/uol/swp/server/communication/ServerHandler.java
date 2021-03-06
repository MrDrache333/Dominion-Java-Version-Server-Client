package de.uol.swp.server.communication;

import de.uol.swp.common.message.RequestMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasse ServerHandler
 * Funktion: Netty-handler für einkommende Kommunikation
 *
 * @author Marco Grawunder
 * @since Sprint 0
 */
@Sharable
class ServerHandler implements ChannelInboundHandler {

    private static final Logger LOG = LogManager.getLogger(ServerHandler.class);

    private final ServerHandlerDelegate delegate;

    /**
     * Erstellt einen neuen ServerHandler
     *
     * @param delegate Soll Informationen über die Verbindung erhalten.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    public ServerHandler(ServerHandlerDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Registriert einen neuen Channel.
     *
     * @param channelHandlerContext Ermöglicht einem ChannelHandler die Interaktion mit seiner ChannelPipeline und anderen Handlern.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) {

    }

    /**
     * Verwirft die Registrierung eines Channels
     *
     * @param channelHandlerContext Ermöglicht einem ChannelHandler die Interaktion mit seiner ChannelPipeline und anderen Handlern.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) {

    }

    /**
     * Aktiviert einen Channel.
     *
     * @param ctx Der ChannelHandlerContext
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        delegate.newClientConnected(ctx);
    }

    /**
     * Deaktiviert einen Channel.
     *
     * @param channelHandlerContext Ermöglicht einem ChannelHandler die Interaktion mit seiner ChannelPipeline und anderen Handlern.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {

    }

    /**
     * Liest in einem Channel. Ist die übergebene Message eine Instanz der RequestMessage,
     * so wird der diese gecastet und der Prozess fortgesetzt.
     *
     * @param ctx Der ChannelHandlerContext
     * @param msg Die Message
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Server ignores everything but IRequestMessages
        if (msg instanceof RequestMessage) {
            delegate.process(ctx, (RequestMessage) msg);
        } else {
            LOG.error("Illegales Objekt aus dem Channel gelesen. Ignoriert!");
        }
    }

    /**
     * Wird am Ende des Lesevorgang von channelRead aufgerufen.
     *
     * @param channelHandlerContext Ermöglicht einem ChannelHandler die Interaktion mit seiner ChannelPipeline und anderen Handlern.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {

    }

    /**
     * Wird aufgerufen, wenn ein User-Event ausgelöst wurde.
     *
     * @param channelHandlerContext Ermöglicht einem ChannelHandler die Interaktion mit seiner ChannelPipeline und anderen Handlern.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object o) {

    }

    /**
     * Wird aufgerufen, wenn sich die Schreibberechtigung eines Channels ändert.
     *
     * @param channelHandlerContext Ermöglicht einem ChannelHandler die Interaktion mit seiner ChannelPipeline und anderen Handlern.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelHandlerContext) {

    }

    /**
     * Wird beim Anlegen eines Handlers aufgerufen.
     *
     * @param channelHandlerContext Ermöglicht einem ChannelHandler die Interaktion mit seiner ChannelPipeline und anderen Handlern.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) {

    }

    /**
     * Wird beim Löschen eines Handlers aufgerufen.
     *
     * @param channelHandlerContext Ermöglicht einem ChannelHandler die Interaktion mit seiner ChannelPipeline und anderen Handlern.
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) {

    }

    /**
     * Fängt eine Exception und gibt diese bei aktivem/geöffnetem Channel aus.
     * Andernfalls wird die Verbindung beendet.
     *
     * @param ctx   Der ChannelHandlerContext
     * @param cause Der Grund
     * @author Marco Grawunder
     * @since Sprint 0
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx.channel().isActive() || ctx.channel().isOpen()) {
            LOG.error("Exception caught " + cause);
        } else {
            delegate.clientDisconnected(ctx);
        }
    }

}

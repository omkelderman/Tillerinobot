package org.tillerino.ppaddict.chat.local;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.tuple.Pair;
import org.tillerino.ppaddict.chat.GameChatEvent;
import org.tillerino.ppaddict.chat.GameChatResponseQueue;
import org.tillerino.ppaddict.chat.impl.ResponsePostprocessor;
import org.tillerino.ppaddict.util.LoopingRunnable;
import org.tillerino.ppaddict.util.MdcUtils;
import org.tillerino.ppaddict.util.MdcUtils.MdcAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tillerino.tillerinobot.CommandHandler.Response;
import tillerino.tillerinobot.rest.BotInfoService.BotInfo;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class LocalGameChatResponseQueue extends LoopingRunnable implements GameChatResponseQueue {
	private final ResponsePostprocessor downstream;

	private final BlockingQueue<Pair<Response, GameChatEvent>> queue = new LinkedBlockingQueue<>();

	private final BotInfo botInfo;

	@Override
	public void onResponse(Response response, GameChatEvent event) throws InterruptedException {
		event.getMeta().setMdc(MdcUtils.getSnapshot());
		queue.put(Pair.of(response, event));
		botInfo.setResponseQueueSize(queue.size());
	}

	@Override
	protected void loop() throws InterruptedException {
		Pair<Response, GameChatEvent> response = queue.take();
		botInfo.setResponseQueueSize(queue.size());
		try (MdcAttributes mdc = response.getRight().getMeta().getMdc().apply()) {
			downstream.onResponse(response.getLeft(), response.getRight());
		} catch (InterruptedException e) {
			throw e;
		} catch (Throwable e) {
			log.error("Exception while handling response", e);
		}
	}

	@Override
	public int size() {
		return queue.size();
	}
}

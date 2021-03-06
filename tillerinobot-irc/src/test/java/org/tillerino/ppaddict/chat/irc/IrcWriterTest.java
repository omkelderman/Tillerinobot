package org.tillerino.ppaddict.chat.irc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;
import org.pircbotx.output.OutputUser;
import org.tillerino.ppaddict.chat.GameChatMetrics;
import org.tillerino.ppaddict.chat.LiveActivity;
import org.tillerino.ppaddict.chat.PrivateMessage;
import org.tillerino.ppaddict.chat.irc.BotRunnerImpl.CloseableBot;

@RunWith(MockitoJUnitRunner.class)
public class IrcWriterTest {
	@Mock
    LiveActivity liveActivity;

	@Mock
	ExecutorService exec;

	@Mock
	Pinger pinger;

	@Mock
	GameChatMetrics botInfo;

	@InjectMocks
	IrcWriter queue;

	@Mock
	CloseableBot bot;

	@Mock
	UserChannelDao<User, Channel> userChannelDao;

	@Mock
	User pircBotXuser;

	@Mock
	OutputUser outputUser;

	@Before
	public void before() {
		queue.setBot(bot);
		when(bot.getUserChannelDao()).thenReturn(userChannelDao);
		when(userChannelDao.getUser("user")).thenReturn(pircBotXuser);
		when(pircBotXuser.send()).thenReturn(outputUser);
	}

	@Test
	public void testMessage() throws Exception {
		queue.message("abc", new PrivateMessage(6789, "user", 12345, "hello"));
		verify(outputUser).message("abc");
	}

	@Test
	public void testAction() throws Exception {
		queue.action("abc", new PrivateMessage(6789, "user", 12345, "hello"));
		verify(outputUser).action("abc");
	}
}

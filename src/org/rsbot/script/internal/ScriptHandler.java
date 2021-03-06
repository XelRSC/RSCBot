package org.rsbot.script.internal;

import org.rsbot.bot.Bot;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.internal.event.ScriptListener;
import org.rsbot.script.randoms.*;

import java.util.*;

public class ScriptHandler {

	private final ArrayList<org.rsbot.script.Random> randoms = new ArrayList<org.rsbot.script.Random>();
	private final HashMap<Integer, Script> scripts = new HashMap<Integer, Script>();
	private final HashMap<Integer, Thread> scriptThreads = new HashMap<Integer, Thread>();

	private final Set<ScriptListener> listeners = Collections.synchronizedSet(new HashSet<ScriptListener>());

	private final Bot bot;

	public ScriptHandler(final Bot bot) {
		this.bot = bot;
	}

	public void init() {
		try {
			randoms.add(new LoginBot());
			randoms.add(new BankPins());
			randoms.add(new BeehiveSolver());
			randoms.add(new CapnArnav());
			randoms.add(new Certer());
			randoms.add(new CloseAllInterface());
			randoms.add(new DrillDemon());
			randoms.add(new FreakyForester());
			randoms.add(new FrogCave());
			randoms.add(new GraveDigger());
			randoms.add(new ImprovedRewardsBox());
			randoms.add(new LostAndFound());
			randoms.add(new Maze());
			randoms.add(new Mime());
			randoms.add(new Molly());
			randoms.add(new Exam());
			randoms.add(new Pillory());
			randoms.add(new Pinball());
			randoms.add(new Prison());
			randoms.add(new QuizSolver());
			randoms.add(new SandwhichLady());
			randoms.add(new ScapeRuneIsland());
			randoms.add(new TeleotherCloser());
			randoms.add(new FirstTimeDeath());
			randoms.add(new LeaveSafeArea());
			randoms.add(new SystemUpdate());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		for (final org.rsbot.script.Random r : randoms) {
			r.init(bot.getMethodContext());
		}
	}

	public void addScriptListener(final ScriptListener l) {
		listeners.add(l);
	}

	public void removeScriptListener(final ScriptListener l) {
		listeners.remove(l);
	}

	private void addScriptToPool(final Script ss, final Thread t) {
		for (int off = 0; off < scripts.size(); ++off) {
			if (!scripts.containsKey(off)) {
				scripts.put(off, ss);
				ss.setID(off);
				scriptThreads.put(off, t);
				return;
			}
		}
		ss.setID(scripts.size());
		scripts.put(scripts.size(), ss);
		scriptThreads.put(scriptThreads.size(), t);
	}

	public Bot getBot() {
		return bot;
	}

	public Collection<org.rsbot.script.Random> getRandoms() {
		return randoms;
	}

	public Map<Integer, Script> getRunningScripts() {
		return Collections.unmodifiableMap(scripts);
	}

	public void pauseScript(final int id) {
		final Script s = scripts.get(id);
		s.setPaused(!s.isPaused());
		if (s.isPaused()) {
			for (final ScriptListener l : listeners) {
				l.scriptPaused(this, s);
			}
		} else {
			for (final ScriptListener l : listeners) {
				l.scriptResumed(this, s);
			}
		}
	}

	public void stopScript(final int id) {
		final Script script = scripts.get(id);
		if (script != null) {
			script.deactivate(id);
			scripts.remove(id);
			scriptThreads.remove(id);
			for (final ScriptListener l : listeners) {
				l.scriptStopped(this, script);
			}
		}
	}

	public boolean onBreak(final int id) {
		final Script script = scripts.get(id);
		return script != null && script.onBreakStart();
	}

	public void onBreakConclude(final int id) {
		final Script script = scripts.get(id);
		if (script != null) {
			script.onBreakFinish();
		}
	}

	public void runScript(final Script script) {
		script.init(bot.getMethodContext());
		for (final ScriptListener l : listeners) {
			l.scriptStarted(this, script);
		}
		final ScriptManifest prop = script.getClass().getAnnotation(ScriptManifest.class);
		final Thread t = new Thread(script, "Script-" + prop.name());
		addScriptToPool(script, t);
		t.start();
	}

	public void stopAllScripts() {
		final Set<Integer> theSet = scripts.keySet();
		final int[] arr = new int[theSet.size()];
		int c = 0;
		for (final int i : theSet) {
			arr[c] = i;
			c++;
		}
		for (final int id : arr) {
			stopScript(id);
		}
	}

	public void stopScript() {
		final Thread curThread = Thread.currentThread();
		for (int i = 0; i < scripts.size(); i++) {
			final Script script = scripts.get(i);
			if (script != null && script.isRunning()) {
				if (scriptThreads.get(i) == curThread) {
					stopScript(i);
				}
			}
		}
		if (curThread == null) {
			throw new ThreadDeath();
		}
	}

	public boolean onBreak() {
		final Thread curThread = Thread.currentThread();
		for (int i = 0; i < scripts.size(); i++) {
			final Script script = scripts.get(i);
			if (script != null && script.isRunning()) {
				if (scriptThreads.get(i) == curThread) {
					return onBreak(i);
				}
			}
		}
		if (curThread == null) {
			throw new ThreadDeath();
		}
		return false;
	}

	public void onBreakResume() {
		final Thread curThread = Thread.currentThread();
		for (int i = 0; i < scripts.size(); i++) {
			final Script script = scripts.get(i);
			if (script != null && script.isRunning()) {
				if (scriptThreads.get(i) == curThread) {
					onBreakConclude(i);
					return;
				}
			}
		}
		if (curThread == null) {
			throw new ThreadDeath();
		}
	}

	public void updateInput(final Bot bot, final int mask) {
		for (final ScriptListener l : listeners) {
			l.inputChanged(bot, mask);
		}
	}

}

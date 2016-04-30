package org.apache.camel.standalone.fsm;

import org.apache.camel.standalone.Standalone;
import org.apache.camel.standalone.fsm.actions.*;
import org.statefulj.fsm.FSM;
import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;
import org.statefulj.fsm.model.impl.StateImpl;
import org.statefulj.persistence.memory.MemoryPersisterImpl;

import java.util.LinkedList;
import java.util.List;

public class StandaloneStateMachine {
    public static final String initializeEvent = "Initialize";
    public static final String startEvent = "Start";
    public static final String stopEvent = "Stop";
    public static final String suspendEvent = "Suspend";
    public static final String resumeEvent = "Resume";
    public static final String shutdownEvent = "Shutdown";

    State<Standalone> newState = new StateImpl<>("New");
    State<Standalone> initializedState = new StateImpl<>("Initialized");
    State<Standalone> startedState = new StateImpl<>("Started");
    State<Standalone> stoppedState = new StateImpl<>("Stopped");
    State<Standalone> suspendedState = new StateImpl<>("Suspended");
    State<Standalone> shutdownState = new StateImpl<>("Shut Down", true);

    List<State<Standalone>> states = new LinkedList<>();

    Action<Standalone> initializeAction = new InitializeAction();
    Action<Standalone> startAction = new StartAction();
    Action<Standalone> stopAction = new StopAction();
    Action<Standalone> suspendAction = new SuspendAction();
    Action<Standalone> resumeAction = new ResumeAction();
    Action<Standalone> shutdownAction = new ShutdownAction();

    final MemoryPersisterImpl<Standalone> persister;

    final FSM<Standalone> fsm;

    public StandaloneStateMachine(Standalone standalone) {
        states.add(newState);
        states.add(initializedState);
        states.add(startedState);
        states.add(stoppedState);
        states.add(suspendedState);
        states.add(shutdownState);

        newState.addTransition(initializeEvent, initializedState, initializeAction);
        initializedState.addTransition(startEvent, startedState, startAction);
        startedState.addTransition(stopEvent, stoppedState, stopAction);
        startedState.addTransition(suspendEvent, suspendedState, suspendAction);
        startedState.addTransition(shutdownEvent, shutdownState, shutdownAction);
        stoppedState.addTransition(startEvent, startedState, startAction);
        suspendedState.addTransition(resumeEvent, startedState, resumeAction);
        suspendedState.addTransition(stopEvent, stoppedState, stopAction);

        persister = new MemoryPersisterImpl<>(standalone, states, newState);

        this.fsm = new FSM<>("Camel Standalone FSM", persister);
    }

    public FSM<Standalone> getFsmInstance() {
        return fsm;
    }
}

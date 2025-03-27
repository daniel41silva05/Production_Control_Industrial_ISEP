package org.project.model;

import java.util.Objects;

public class Event implements Comparable<Event> {

    private ProductionElement element;
    private Workstation workStation;
    private int startTime;
    private int endTime;

    public Event(ProductionElement element, Workstation workStation, int startTime, int endTime) {
        this.element = element;
        this.workStation = workStation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ProductionElement getElement() {
        return element;
    }

    public void setElement(ProductionElement element) {
        this.element = element;
    }

    public Workstation getWorkStation() {
        return workStation;
    }

    public void setWorkStation(Workstation workStation) {
        this.workStation = workStation;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Event event = (Event) object;
        return startTime == event.startTime && endTime == event.endTime && Objects.equals(element, event.element) && Objects.equals(workStation, event.workStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, workStation, startTime, endTime);
    }

    @Override
    public int compareTo(Event o) {
        return Integer.compare(this.getEndTime(), o.getEndTime());
    }

    @Override
    public String toString() {
        return "Event{" +
                "element=" + element +
                ", workStation=" + workStation +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}
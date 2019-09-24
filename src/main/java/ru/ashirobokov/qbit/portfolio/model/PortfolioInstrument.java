package ru.ashirobokov.qbit.portfolio.model;

public class PortfolioInstrument {
    Long instrumentId;
    Long number;

    public PortfolioInstrument(Long instrumentId, Long number) {
        this.instrumentId = instrumentId;
        this.number = number;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public Long getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "{" +
                "instrumentId=" + instrumentId +
                ", number=" + number +
                '}';
    }
}

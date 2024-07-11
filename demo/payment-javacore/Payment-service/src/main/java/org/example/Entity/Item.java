package org.example.Entity;

public class Item {
    String note;
    String quantity;
    String qrInfor;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQrInfor() {
        return qrInfor;
    }

    public void setQrInfor(String qrInfor) {
        this.qrInfor = qrInfor;
    }

    @Override
    public String toString() {
        return "Item{" +
                "note='" + note + '\'' +
                ", quantity='" + quantity + '\'' +
                ", qrInfor='" + qrInfor + '\'' +
                '}';
    }
}

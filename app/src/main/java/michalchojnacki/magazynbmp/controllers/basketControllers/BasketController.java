package michalchojnacki.magazynbmp.controllers.basketControllers;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import michalchojnacki.magazynbmp.model.SparePart;

public class BasketController implements Serializable {

    private List<SparePartWithQuantity> mSparePartsWithQuantities = new LinkedList<>();

    public void addToBasket(SparePart sparePart, int quantity) {
        mSparePartsWithQuantities.add(new SparePartWithQuantity(sparePart).setQuantity(quantity));
    }

    public void updateSparePart(SparePart sparePart) {

    }

    public void deleteSparePart(SparePart sparePart) {

    }

    public int size() {
        return mSparePartsWithQuantities.size();
    }

    public SparePart getSparePart(int index) {
        return mSparePartsWithQuantities.get(index).getSparePart();
    }

    public int getQuantity(int index) {
        return mSparePartsWithQuantities.get(index).getQuantity();
    }

    class SparePartWithQuantity implements Serializable {

        private SparePart mSparePart;
        private int quantity = 0;

        public SparePartWithQuantity(SparePart sparePart) {
            mSparePart = sparePart;
        }

        public int getQuantity() {
            return quantity;
        }

        public SparePartWithQuantity setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public SparePart getSparePart() {
            return mSparePart;
        }
    }
}

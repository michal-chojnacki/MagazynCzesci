package michalchojnacki.magazynbmp.controllers.resControllers.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import michalchojnacki.magazynbmp.R;
import michalchojnacki.magazynbmp.controllers.basketControllers.BasketController;
import michalchojnacki.magazynbmp.controllers.basketControllers.QuantityChangedListener;
import michalchojnacki.magazynbmp.controllers.recyclerViews.DividerItemDecoration;
import michalchojnacki.magazynbmp.controllers.resControllers.dialogs.ChangeBasketDialog;
import michalchojnacki.magazynbmp.controllers.resControllers.dialogs.QuestionDialog;
import michalchojnacki.magazynbmp.controllers.resControllers.listeners.ItemClickListener;
import michalchojnacki.magazynbmp.model.SparePart;

public class BasketViewer extends AppCompatActivity {

    public static final String BASKET_CONTROLLER = "basketController";
    private BasketController mBasketController;
    private BasketRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basket, menu);
        MenuItem clearBasket = menu.findItem(R.id.MenuClearBasket);
        clearBasket.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                QuestionDialog.newInstance("Clear?", "Clear basket?")
                        .setPositiveClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBasketController.clear();
                                recyclerViewAdapter.notifyDataSetChanged();

                            }
                        }).showDialog(BasketViewer.this);
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_parts_tray);

        recyclerViewAdapter = getRecyclerViewAdapter();
        createRecyclerView(recyclerViewAdapter);
    }

    @NonNull
    private BasketRecyclerViewAdapter getRecyclerViewAdapter() {
        mBasketController = (BasketController) getIntent().getSerializableExtra(BASKET_CONTROLLER);
        return new BasketRecyclerViewAdapter(this, mBasketController);
    }

    private void createRecyclerView(BasketRecyclerViewAdapter recyclerViewAdapter) {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.SparePartsTrayRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
    }
}

class BasketRecyclerViewAdapter extends RecyclerView.Adapter<BasketRecyclerViewAdapter.SparePartsViewHolder> {

    private final Context mContext;
    private BasketController mBasketController;

    public BasketRecyclerViewAdapter(Context context, BasketController basketController) {
        mContext = context;
        this.mBasketController = basketController;
    }

    @Override
    public SparePartsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_spare_parts_tray_item, parent, false);
        return new SparePartsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SparePartsViewHolder holder, int position) {
        holder.mNumber.setText(mBasketController.getSparePart(position).getNumber());
        holder.mQuantity.setText(String.valueOf(mBasketController.getQuantity(position)));
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position) {
                SparePart sparePart = mBasketController.getSparePart(position);
                Intent intent = new Intent(mContext, SparePartViewer.class);
                intent.putExtra(SparePartViewer.SPARE_PART, sparePart);
                intent.putExtra(SparePartViewer.BASKET_CONTROLLER, mBasketController);
                mContext.startActivity(intent);
            }
        });
        holder.setLongClickListener(new ItemClickListener() {
            @Override
            public void onClick(final int position) {
                ChangeBasketDialog
                        .newInstance(mBasketController.getSparePart(position), mBasketController.getQuantity(position))
                        .setDeleteClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                QuestionDialog.newInstance("Are You sure?", "do you want to del?")
                                        .setPositiveClickListener(new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mBasketController.deleteSparePart(mBasketController.getSparePart(position));
                                                notifyDataSetChanged();
                                            }
                                        }).showDialog(mContext);
                            }
                        })
                        .setChangeClick(new QuantityChangedListener() {
                            @Override
                            public void quantityChanged(int newQuantity) {
                                mBasketController.addToBasket(mBasketController.getSparePart(position), newQuantity);
                                notifyDataSetChanged();
                            }
                        }).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBasketController.size();
    }

    static class SparePartsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final TextView mNumber;
        private final TextView mQuantity;
        private ItemClickListener clickListener;
        private ItemClickListener longClickListener;

        public SparePartsViewHolder(View itemView) {
            super(itemView);
            mNumber = (TextView) itemView.findViewById(R.id.SparePartsTrayNumberItem);
            mQuantity = (TextView) itemView.findViewById(R.id.SparePartsTrayQuantityItem);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            longClickListener.onClick(getPosition());
            return false;
        }

        public void setLongClickListener(ItemClickListener longClickListener) {
            this.longClickListener = longClickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(getPosition());
        }

        public void setClickListener(ItemClickListener clickListener) {
            this.clickListener = clickListener;
        }


    }
}
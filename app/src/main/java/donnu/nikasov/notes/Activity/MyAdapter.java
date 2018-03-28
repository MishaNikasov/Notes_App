package donnu.nikasov.notes.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import donnu.nikasov.notes.Data.ArchiveData;
import donnu.nikasov.notes.Data.NotesData;
import donnu.nikasov.notes.R;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public static ArrayList<NotesData> data;
    private ArrayList<NotesData> dataCopy;
    private Context context;
    private String type;

    MyAdapter(Context context, ArrayList<NotesData> dataset, String type) {

        this.type = type;
        this.context = context;
        data = dataset;
        dataCopy = new ArrayList<>();
        dataCopy.addAll(dataset);
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tagView.setText(String.valueOf(data.get(position).getTag()));
        holder.titleView.setText(data.get(position).getTitle());
        holder.describeView.setText(data.get(position).getDescribe());

        if (data.get(position).getColor()!=null) {
                String color = data.get(position).getColor();
                switch (color) {
                    case "Лиловый":
                        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.primaryColor));
                        break;
                    case "Розовый":
                        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.pink));
                        break;
                    case "Зеленый":
                        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.green));
                        break;
                    case "Бриз":
                        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.blue));
                        break;
                    case "Оранжевый":
                        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.orange));
                        break;
                    case "Синий":
                        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.darkBlue));
                        break;
                    case "Желтый":
                        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.yellow));
                        break;
                }
            }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView titleView;
        private TextView tagView;
        private TextView describeView;

        ViewHolder(View v) {
            super(v);
            Typeface segoe = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/segoe.ttf");
            Typeface segoeBold = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/segoeBold.ttf");
            Typeface segoeNormal = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/segoeNormal.ttf");

            cardView = (CardView) itemView.findViewById(R.id.notesCardItem);

            titleView = (TextView) v.findViewById(R.id.mainText);
            tagView = (TextView) v.findViewById(R.id.tagText);
            describeView = (TextView) v.findViewById(R.id.describeText);

            tagView.setTypeface(segoe);
            titleView.setTypeface(segoeBold);
            describeView.setTypeface(segoe);

            if (type.equals("main")) {

                cardView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(final View v) {

                        final int position = getAdapterPosition();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());

                        alertDialog.setItems(new CharSequence[]{"Поделиться", "Архивировать", "Удалить"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        String text;
                                        text = data.get(position).getTitle() + "\n" +
                                                data.get(position).getDescribe();

                                        createShareIntent(text);
                                        break;
                                    case 1:
                                        Random r = new Random();
                                        int i1 = r.nextInt(1000);

                                        NotesData note = new NotesData(data.get(position).getTitle(), data.get(position).getDescribe(),
                                                data.get(position).getDate(), data.get(position).getTag(), data.get(position).getColor(), i1);

                                        NotesData.addNoteToArchive(context, data.get(position), note);
                                        data.remove(position);

                                        notifyDataSetChanged();

                                        MainActivity.checkEmptyness();
                                        break;
                                    case 2:
                                        NotesData.deleteNoteFromDataList(v.getContext(), data.get(position));

                                        data.remove(position);
                                        notifyDataSetChanged();
                                        MainActivity.checkEmptyness();

                                        break;
                                }
                            }
                        });

                        alertDialog.create();
                        alertDialog.show();

                        return true;
                    }
                });

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position = getAdapterPosition();

                        Intent intent = new Intent(v.getContext(), EditNoteActivity.class);

                        intent.putExtra("EditNote", data.get(position));
                        intent.putExtra("Position", position);
                        intent.putExtra("Type", "main");

                        v.getContext().startActivity(intent);
                    }
                });
            }

            else {
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position = getAdapterPosition();

                        Intent intent = new Intent(v.getContext(), EditNoteActivity.class);

                        intent.putExtra("EditNote", data.get(position));
                        intent.putExtra("Position", position);
                        intent.putExtra("Type", "archive");

                        v.getContext().startActivity(intent);
                    }
                });

                cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        final int position = getAdapterPosition();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());

                        alertDialog.setItems(new CharSequence[]{"Поделиться", "Восстановить","Удалить"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        String text;
                                        text = ArchiveData.notesArchiveDataList.get(position).getTitle() + "\n" +
                                                ArchiveData.notesArchiveDataList.get(position).getDescribe();

                                        createShareIntent(text);
                                        break;

                                    case 1:

                                        NotesData note = new NotesData(data.get(position).getTitle(), data.get(position).getDescribe(),
                                                data.get(position).getDate(), data.get(position).getTag(), data.get(position).getColor());

                                        ArchiveData.addNoteToMainList(context, data.get(position), note);
                                        data.remove(position);

                                        notifyDataSetChanged();
                                        MainActivity.checkEmptyness();

                                        break;

                                    case 2:
                                        ArchiveData.deleteNoteFromDataList(v.getContext(), ArchiveData.notesArchiveDataList.get(position));
                                        data.remove(position);
                                        notifyDataSetChanged();
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, ArchiveData.notesArchiveDataList.size());
                                        MainActivity.checkEmptyness();

                                        break;
                                }
                            }
                        });

                        alertDialog.create();
                        alertDialog.show();

                        return true;
                    }
                });
            }
        }
    }

    public void searchInList(String text) {

        data.clear();
        if(text.isEmpty()){

            data.addAll(dataCopy);
        } else{

            text = text.toLowerCase();

            for(NotesData item: dataCopy){
                if(item.getTitle().toLowerCase().contains(text) || item.getDescribe().toLowerCase().contains(text)){
                    data.add(item);
                }
            }
        }
        notifyDataSetChanged();
        MainActivity.checkEmptyness();
    }


    public void sortByTag(String text) {

        data.clear();
        if(text.isEmpty()){
            data.addAll(dataCopy);
        } else{

            text = text.toLowerCase();

            for(NotesData item: dataCopy){
                System.out.println(item.getTag());
                if (item.getTag()!=null){
                    if(item.getTag().toLowerCase().contains(text)){
                        data.add(item);
                    }
                }
            }
        }

        notifyDataSetChanged();
        MainActivity.checkEmptyness();
    }

    private void createShareIntent(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(sendIntent);
    }
}
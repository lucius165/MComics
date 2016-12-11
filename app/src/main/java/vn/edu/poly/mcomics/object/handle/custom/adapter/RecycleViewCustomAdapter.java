package vn.edu.poly.mcomics.object.handle.custom.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.poly.mcomics.R;
import vn.edu.poly.mcomics.activity.ComicDetailActivity;
import vn.edu.poly.mcomics.object.variable.Comics;

/**
 * Created by lucius on 11/15/16.
 */

public class RecycleViewCustomAdapter extends RecyclerView.Adapter<RecycleViewCustomAdapter.RecyclerViewHolder> {

    private ArrayList<Comics> arrComics = new ArrayList<>();

    public RecycleViewCustomAdapter(ArrayList<Comics> arrComics) {
        this.arrComics = arrComics;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_top_list, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final Context context = holder.comicImage.getContext();
        holder.comicName.setText(arrComics.get(position).comicsName);
        holder.viewNumber.setText(arrComics.get(position).getViews() + "");
        holder.chapterNumber.setText(arrComics.get(position).getEpisodes() + "");
        Picasso.with(context).load(arrComics.get(position).thumbnail).error(R.mipmap.bia).resize(300, 400).into(holder.comicImage);
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ComicDetailActivity.class);
                intent.putExtra("id", arrComics.get(position).getId() + "");
                intent.putExtra("comicsName", arrComics.get(position).getComicsName());
                intent.putExtra("view", String.valueOf(arrComics.get(position).getViews()));
                intent.putExtra("content", arrComics.get(position).getContent());
                intent.putExtra("thumbnail", arrComics.get(position).getThumbnail());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrComics.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView comicName, viewNumber, chapterNumber;
        ImageView comicImage;
        LinearLayout linear;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            comicName = (TextView) itemView.findViewById(R.id.txtHeader);
            comicImage = (ImageView) itemView.findViewById(R.id.imgItem);
            viewNumber = (TextView) itemView.findViewById(R.id.view_number);
            chapterNumber = (TextView) itemView.findViewById(R.id.chapter_number);

        }

    }
}

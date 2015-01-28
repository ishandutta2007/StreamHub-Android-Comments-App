package com.livefyre.comments.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livefyre.comments.AppSingleton;
import com.livefyre.comments.ImagesCache.ImagesCache;
import com.livefyre.comments.LFCApplication;
import com.livefyre.comments.LFSAppConstants;
import com.livefyre.comments.LFUtils;
import com.livefyre.comments.R;
import com.livefyre.comments.models.ContentBean;
import com.livefyre.comments.models.Vote;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kvanadev5 on 25/01/15.
 */
public class CommentsAdapter extends BaseAdapter {
    protected LFCApplication application = AppSingleton.getInstance().getApplication();
    private LayoutInflater inflater;
    Context mContext;
    private ImagesCache cache;
    private Bitmap image = null;
    private List<ContentBean> contentArray=null;

    public CommentsAdapter(Context mContext, List<ContentBean> contentArray) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.contentArray = contentArray;
    }

    @Override
    public int getCount() {
        return contentArray.size();
    }

    @Override
    public Object getItem(int position) {
        return contentArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder {
        TextView authorNameTv, postedDateOrTime, commentBody, moderatorTv, likesTv;

        LinearLayout featureLL;

        ImageView avatarIv,imageAttachedToCommentIv;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = getConvertView();

        ViewHolder holder = (ViewHolder) convertView.getTag();
        updateView(position, holder);

        convertView.setId(position);

        return convertView;
    }

    private void updateView(int position, ViewHolder holder) {
        final ContentBean comment = contentArray.get(position);
        try {




            //Author Name
            holder.authorNameTv.setText(comment.getAuthor().getDisplayName());
            //Posted Date
            holder.postedDateOrTime.setText(LFUtils.getFormatedDate(
                    comment.getCreatedAt(), LFSAppConstants.SHART));
            //Comment Body
            holder.commentBody.setText(LFUtils.trimTrailingWhitespace(Html
                            .fromHtml(comment.getBodyHtml())),
                    TextView.BufferType.SPANNABLE);
            //Moderator
            if (comment.getIsModerator().equals("true")) {
                holder.moderatorTv.setVisibility(View.VISIBLE);
            } else {
                holder.moderatorTv.setVisibility(View.GONE);
            }
            //Featured
            if (comment.getIsFeatured()) {
                holder.moderatorTv.setVisibility(View.GONE);
                holder.featureLL.setVisibility(View.VISIBLE);
            } else {
                holder.featureLL.setVisibility(View.GONE);
            }

            //Liked
            if (comment.getVote() != null) {
                if (comment.getVote().size() > 0) {
                    application.printLog(true, "vote", contentArray.get(position).getVote().size() + "A");
                    holder.likesTv.setVisibility(View.VISIBLE);
                    holder.likesTv.setText(likedCount(comment.getVote()));
                } else
                    holder.likesTv.setVisibility(View.GONE);
            } else
                holder.likesTv.setVisibility(View.GONE);




            if (comment.getAuthor().getAvatar().length() > 0) {
//                Bitmap bm = cache.getImageFromWarehouse(comment.getAuthor()
//                        .getAvatar());
//
//                if (bm != null) {
//                    holder.avatarIv.setImageBitmap(bm);
//                } else {
//                    holder.avatarIv.setImageBitmap(null);
//                    DownloadImageTask imgTask = new DownloadImageTask(this);
//                    imgTask.execute(comment.getAuthor().getAvatar());
//
//                }
                Picasso.with(mContext).load(comment.getAuthor().getAvatar()).fit()
                        .into(holder.avatarIv);
            } else {
//                holder.avatarIv
//                        .setImageResource(R.drawable.profile_default);
            }
//            DownloadImageTask.getRoundedShape(holder.avatarIv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public View getConvertView() {
        ViewHolder holder = new ViewHolder();
        View view = null;

        view = inflater.inflate(R.layout.comments_list_item, null);
        holder.authorNameTv = (TextView) view.findViewById(R.id.authorNameTv);
        holder.postedDateOrTime = (TextView) view.findViewById(R.id.postedDateOrTime);
        holder.commentBody = (TextView) view.findViewById(R.id.commentBody);
        holder.likesTv = (TextView) view.findViewById(R.id.likesTv);
        holder.moderatorTv = (TextView) view.findViewById(R.id.moderatorTv);
        holder.featureLL = (LinearLayout) view.findViewById(R.id.featureLL);

        holder.avatarIv = (ImageView) view.findViewById(R.id.avatarIv);
        holder.imageAttachedToCommentIv = (ImageView) view.findViewById(R.id.imageAttachedToCommentIv);

        view.setTag(holder);
        return view;
    }

    String likedCount(List<Vote> v) {
        int count = 0;
        for (int i = 0; i < v.size(); i++) {
            if (v.get(i).getValue().equals("1"))
                count++;
        }
        return "Likes " + v.size();
    }

}

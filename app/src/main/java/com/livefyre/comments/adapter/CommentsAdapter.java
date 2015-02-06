package com.livefyre.comments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livefyre.comments.AppSingleton;
import com.livefyre.comments.LFCApplication;
import com.livefyre.comments.LFSAppConstants;
import com.livefyre.comments.LFUtils;
import com.livefyre.comments.R;
import com.livefyre.comments.RoundedTransformation;
import com.livefyre.comments.models.ContentBean;
import com.livefyre.comments.models.Vote;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kvanadev5 on 02/02/15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    private LFCApplication application = AppSingleton.getInstance().getApplication();
    private LayoutInflater mLayoutInflater;
    Context mContext;
    private List<ContentBean> contentArray = null;

    public CommentsAdapter(Context context, List<ContentBean> contentArray) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.contentArray = contentArray;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.comments_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ContentBean comment = contentArray.get(position);



        try {

            float density = mContext.getResources().getDisplayMetrics().density;

            int px = (int) (40 * density);

            switch (comment.getDepth()) {
                case 0:
                    holder.commentsListItemLL.setPadding(16, 0, 16, 16);
                    break;
                case 1:
                    holder.commentsListItemLL.setPadding(px * 1, 0, 16, 16);
                    break;
                case 2:
                    holder.commentsListItemLL.setPadding(px * 2, 0, 16, 16);
                    break;
                case 3:
                    holder.commentsListItemLL.setPadding(px * 3, 0, 16, 16);
                    break;
                default:
                    holder.commentsListItemLL.setPadding(px * 3, 0, 16, 16);
                    break;

            }

            holder.bottomLine.setVisibility(View.VISIBLE);

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
                Picasso.with(mContext).load(comment.getAuthor().getAvatar()).fit().transform(new RoundedTransformation(90, 0)).into(holder.avatarIv);
            } else {
            }


            if (comment.getOembedUrl() != null) {
                if (comment.getOembedUrl().length() > 0) {
                    holder.imageAttachedToCommentIv.setVisibility(View.VISIBLE);
                    application.printLog(true,"comment.getOembedUrl()",comment.getOembedUrl()+" URL");
                    Picasso.with(mContext).load(comment.getOembedUrl()).fit().into(holder.imageAttachedToCommentIv);
                } else {
                    holder.imageAttachedToCommentIv.setVisibility(View.GONE);
                }
            } else {
                holder.imageAttachedToCommentIv.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return contentArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View bottomLine;

        TextView authorNameTv, postedDateOrTime, commentBody, moderatorTv, likesTv;

        LinearLayout featureLL, commentsListItemLL;

        ImageView avatarIv, imageAttachedToCommentIv;

        public MyViewHolder(View item) {
            super(item);

            bottomLine = item.findViewById(R.id.bottomLine);

            commentsListItemLL = (LinearLayout) item.findViewById(R.id.commentsListItemLL);

            authorNameTv = (TextView) item.findViewById(R.id.authorNameTv);
            postedDateOrTime = (TextView) item.findViewById(R.id.postedDateOrTime);
            commentBody = (TextView) item.findViewById(R.id.commentBody);
            likesTv = (TextView) item.findViewById(R.id.likesFullTv);
            moderatorTv = (TextView) item.findViewById(R.id.moderatorTv);
            featureLL = (LinearLayout) item.findViewById(R.id.featureLL);

            avatarIv = (ImageView) item.findViewById(R.id.avatarIv);
            imageAttachedToCommentIv = (ImageView) item.findViewById(R.id.imageAttachedToCommentIv);
        }
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

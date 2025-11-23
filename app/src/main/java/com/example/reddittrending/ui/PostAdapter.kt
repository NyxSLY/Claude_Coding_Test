package com.example.reddittrending.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.reddittrending.R
import com.example.reddittrending.databinding.ItemPostBinding
import com.example.reddittrending.model.RedditPost

class PostAdapter(
    private val onPostClick: (RedditPost) -> Unit
) : ListAdapter<RedditPost, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onPostClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val onPostClick: (RedditPost) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: RedditPost) {
            binding.apply {
                tvTitle.text = post.title
                tvSubreddit.text = post.subredditPrefixed
                tvAuthor.text = "u/${post.author}"
                tvScore.text = post.getFormattedScore()
                tvComments.text = "${post.numComments} 评论"
                tvTime.text = post.getTimeAgo()

                // 显示flair标签
                if (!post.flairText.isNullOrEmpty()) {
                    tvFlair.visibility = View.VISIBLE
                    tvFlair.text = post.flairText
                } else {
                    tvFlair.visibility = View.GONE
                }

                // 显示NSFW标签
                tvNsfw.visibility = if (post.isNsfw) View.VISIBLE else View.GONE

                // 加载缩略图
                val thumbnailUrl = post.preview?.images?.firstOrNull()?.source?.getDecodedUrl()
                    ?: post.thumbnail

                if (!thumbnailUrl.isNullOrEmpty() &&
                    thumbnailUrl.startsWith("http") &&
                    !thumbnailUrl.contains("self") &&
                    !thumbnailUrl.contains("default") &&
                    !thumbnailUrl.contains("nsfw")) {
                    ivThumbnail.visibility = View.VISIBLE
                    ivThumbnail.load(thumbnailUrl) {
                        crossfade(true)
                        placeholder(R.drawable.placeholder_image)
                        error(R.drawable.placeholder_image)
                        transformations(RoundedCornersTransformation(8f))
                    }
                } else {
                    ivThumbnail.visibility = View.GONE
                }

                // 显示帖子内容预览（如果是文字帖）
                if (post.isSelf && !post.selfText.isNullOrEmpty()) {
                    tvPreview.visibility = View.VISIBLE
                    tvPreview.text = post.selfText.take(200).let {
                        if (post.selfText.length > 200) "$it..." else it
                    }
                } else {
                    tvPreview.visibility = View.GONE
                }

                root.setOnClickListener {
                    onPostClick(post)
                }
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<RedditPost>() {
        override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return oldItem == newItem
        }
    }
}

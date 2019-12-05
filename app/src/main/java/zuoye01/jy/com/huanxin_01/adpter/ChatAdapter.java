package zuoye01.jy.com.huanxin_01.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import zuoye01.jy.com.huanxin_01.R;

public class ChatAdapter extends RecyclerView.Adapter {

    public ArrayList<EMMessage> list;
    private OnItemClickListener mListener;

    public ChatAdapter(ArrayList<EMMessage> mlist) {
        list = mlist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder holder1 = (MyHolder) holder;
        EMMessage emMessage = list.get(position);
//        //消息体
//        emMessage.getBody();
//        //发送的时间
//        emMessage.getMsgTime();
//        //发送人
//        emMessage.getFrom();
//        //收信人
//        emMessage.getTo();

        //格式化日期
        //new SimpleDateFormat("yyyyMMdd-hh:mm").format()

        holder1.tv.setText("发送人:"+emMessage.getFrom()+",发送给:"+emMessage.getTo()+
                ",时间:"+new SimpleDateFormat("yyyy-MM-dd-hh:mm").format
                (emMessage.getMsgTime())+",消息内容:"+emMessage.getBody());

        //如果是语音需要点播放  如果是文本啥也不做
        EMMessage.Type type = emMessage.getType();
        if (type == EMMessage.Type.VOICE){
            holder1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null){
                        mListener.OnItemClickListener(view,position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyHolder extends RecyclerView.ViewHolder {

        private final TextView tv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }
    }

    public interface OnItemClickListener {
        void OnItemClickListener(View v,int pos);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

}

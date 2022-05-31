package DiscordBot;

import net.dv8tion.jda.api.EmbedBuilder;


public class EmbedLinkedList implements Cloneable
{
    private EmbedNode head;
    private EmbedNode tail;
    private EmbedNode current;
    private int size = 0;

    public void addBack(EmbedBuilder info)
    {
        size++;
        if (head == null)
        {
            head = new EmbedNode(info, null, null);
            tail = head;
        }
        else
        {
            EmbedNode node = new EmbedNode(info, null, tail);
            this.tail.next = node;
            this.tail = node;
        }
        tail.next = head;
        head.prev = tail;
    }

    public void addFront(EmbedBuilder info)
    {
        size++;
        if (head == null)
        {
            head = new EmbedNode(info, null, null);
            tail = head;
        }
        else
        {
            EmbedNode node = new EmbedNode(info, head, null);
            this.head.prev = node;
            this.head = node;
        }
        tail.next = head;
        head.prev = tail;
    }

    public EmbedBuilder removeBack()
    {
        EmbedBuilder result = null;
        if (head != null)
        {
            size--;
            result = tail.info;
            if (tail.prev != null)
            {
                tail.prev.next = null;
                tail = tail.prev;
                tail.next = head;
                head.prev = tail;
            }
            else
            {
                head = null;
                tail = null;
            }
        }
        return result;
    }

    public EmbedBuilder removeFront()
    {
        EmbedBuilder result = null;
        if (head != null)
        {
            size--;
            result = head.info;
            if (head.next != null)
            {
                head.next.prev = null;
                head = head.next;
                tail.next = head;
                head.prev = tail;
            }
            else
            {
                head = null;
                tail = null;
            }
        }
        return result;
    }

    public EmbedBuilder getHeadBuilder()
    {
        return head.info;
    }

    public EmbedBuilder getTailBuilder()
    {
        return tail.info;
    }

    public void setCurrent(EmbedBuilder eb)
    {
        EmbedNode it = head;
        while (it.info != eb)
        {
            it = it.next;
        }
        current = it;
    }

    public void decrementCurrent()
    {
        current = current.prev;
    }

    public void incrementCurrent()
    {
        current = current.next;
    }


    public EmbedBuilder getCurrentBuilder() {return current.info;}

    @Override
    public EmbedLinkedList clone()
    {
        EmbedLinkedList clone = new EmbedLinkedList();
        EmbedNode itr = this.head;
        for (int i = 0; i < size; i++)
        {

            clone.addBack(new EmbedBuilder(itr.info));
            if (itr == current)
                clone.current = clone.tail;
            itr = itr.next;
        }
        return clone;
    }


    public class EmbedNode
    {
        private EmbedNode next;
        private EmbedNode prev;
        private EmbedBuilder info;

        public EmbedNode(EmbedBuilder info, EmbedNode next, EmbedNode prev)
        {
            this.next = next;
            this.prev = prev;
            this.info = info;
        }

        public void setInfo(EmbedBuilder info)
        {
            this.info = info;
        }

        public void setNext(EmbedNode next)
        {
            this.next = next;
        }

        public void setPrev(EmbedNode prev)
        {
            this.prev = prev;
        }

        public EmbedNode getNext()
        {
            return next;
        }

        public EmbedNode getPrev()
        {
            return prev;
        }

        public EmbedBuilder getInfo()
        {
            return info;
        }
    }
}
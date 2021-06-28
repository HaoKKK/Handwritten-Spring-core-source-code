package test;

class Solution {
    public int findKthNumber(int n, int k) {
        if(k == 1 && n>=1){return 1;}
        if(k == 1){return -1;}
        k--;
        int cur = 1;
        int next;
        int res = -1;
        long count;
        while (k != 0){
            next = cur + 1;
            count = countToNext(cur,next,n);
            if(k >= count){
                k -= count;
                cur = next;
            }else {
                k--;
                cur *= 10;
            }
            if(k == 0){res = cur;}
        }
        return res;
    }

    private long countToNext(int cur, int next,int limit) {
        long count = 1;
        long kk = 10;
        while (cur * kk <= limit){
            if(next * kk <= limit){
                count += next * kk - cur * kk;
            } else {
                count += limit - cur * kk + 1;
            }
            kk *= 10;
        }
        return count;
    }
}
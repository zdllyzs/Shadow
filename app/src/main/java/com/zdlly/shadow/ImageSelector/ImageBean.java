package com.zdlly.shadow.ImageSelector;

    class ImageBean{
        private String topImagePath;
        private String folderName;
        private int imageCounts;

        String getTopImagePath() {
            return topImagePath;
        }
        void setTopImagePath(String topImagePath) {
            this.topImagePath = topImagePath;
        }
        String getFolderName() {
            return folderName;
        }
        void setFolderName(String folderName) {
            this.folderName = folderName;
        }
        int getImageCounts() {
            return imageCounts;
        }
        void setImageCounts(int imageCounts) {
            this.imageCounts = imageCounts;
        }

    @Override
    public String toString() {
        return "ImageBean{" +
                "topImagePath='" + topImagePath + '\'' +
                ", folderName='" + folderName + '\'' +
                ", imageCounts=" + imageCounts +
                '}';
    }
}


<template>
    <div>
        <el-upload
            action="/doc-search/doc/uploadDoc"
            multiple
            on-change
            name="file"
            ref="upload"
            :on-change="handleChange"
            :on-success="handleSuccess"
            :on-error="handleError"
            :on-progress="handleProgress"
            :auto-upload="false"
            :show-file-list="false"
            >
            <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
            <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传到服务器</el-button>
            <el-button style="margin-left: 10px;" size="small" type="info" @click="clearUpload">清空已上传</el-button>
        </el-upload>
        <el-row style="margin-top: 10px;">
            <el-col :span="4" v-for="(item, index) in fileList2" :key="item.url" :offset="index%5>0?1:0">
                <el-card style="margin-bottom: 50px;height: 200px;"  :body-style="{ padding: '0px' }">
                    <div style="text-align: center"><i style="font-size: 80px;color: red;" :class="item.className"></i></div>
                    <div style="padding: 5px;">
                        <span style="font-size: 12px;display: inline-block">文件大小:{{item.sizeText}}</span>
                        <span class="text_display">文件名:{{item.name}}</span>
                        <div style="text-align: center;" class="bottom clearfix">
                            <el-button v-show="item.percentage==0" @click="deleteDoc(index)" type="text" style="color: red" class="button">删除</el-button>
                        </div>
                        <el-progress :percentage="item.percentage" ></el-progress>
                    </div>

                </el-card>

            </el-col>
        </el-row>
        <!--<el-upload-->
            <!--class="upload-demo"-->
            <!--:auto-upload="false"-->
            <!--drag-->
            <!--action="https://jsonplaceholder.typicode.com/posts/"-->
            <!--multiple>-->
            <!--<i class="el-icon-upload"></i>-->
            <!--<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>-->
        <!--</el-upload>-->
    </div>
</template>

<script>
  export default {
      name: "UploadDoc",
      data(){
          return {
              fileList:[],
              fileList2:[]
          }
      },
      methods:{
          submitUpload() {
              this.$refs.upload.submit();
          },
          handleSuccess(response, file, fileList){
              //console.log('handleSuccess',response,file,fileList);
          },
          handleError(err, file, fileList){
              //console.log('handleError',err,file,fileList);
          },
          handleProgress(event, file, fileList){
              //console.log('handleProgress',file,fileList);
              file.percentage = parseInt(file.percentage);
              this.fileList2 = fileList;
          },
          handleChange(file, fileList) {
              var self  = this;
              //console.log("handleChange",file,fileList);
              var accept = ['pdf','xls','txt','xlsx','doc','docx','ppt','pptx'];
              var suffix = self.$utils.getSuffix(file.name);
              if(!self.$utils.contains(accept,suffix)){
                  fileList.splice(fileList.length-1,1);
              }
              // var i = fileList.length;
              // while(i--){
              //     var suffix = self.$utils.getSuffix(fileList[i].name);
              //     if(!self.$utils.contains(accept,suffix)){
              //         fileList.splice(i,1);
              //     }
              // }
              if(!file.sizeText){
                  file.sizeText = self.$utils.getSize(file.size);
              }
              if(!file.className){
                  file.className = self.$utils.getIcon(file.name);
              }
              self.fileList2=fileList;
          },
          deleteDoc(index){
              var self  = this;
              var fileList = this.$refs.upload.uploadFiles;
              fileList.splice(index,1);
              this.fileList2 = fileList;
          },
          clearUpload(){
              var fileList = this.$refs.upload.uploadFiles;
              var self  = this;
              var i = fileList.length;
              while(i--){
                  if(fileList[i].percentage==100){
                      fileList.splice(i,1);
                  }
              }
              this.fileList2 = fileList;
          }
      }

  }
</script>

<style scoped>
    .text_display{
        font-size: 12px;
        display: inline-block;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        word-break: break-all;
        -webkit-box-orient: vertical;
    }
</style>

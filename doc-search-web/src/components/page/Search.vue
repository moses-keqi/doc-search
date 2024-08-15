<template>
    <div>
    <el-form :inline="true" style="text-align: center">
        <el-form-item >
            <el-input style="width: 400px;" v-model="query.words"></el-input>
        </el-form-item>
        <el-button type="primary" :loading="searchLoading"  @click="simpleQuery">搜索</el-button>
        <el-popover
            placement="bottom"
            width="500"
            trigger="click">
            <el-form  label-width="100px">
                <el-form-item label="时间范围">
                    <el-date-picker
                        v-model="query.dateRange"
                        type="daterange"
                        align="right"
                        unlink-panels
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :picker-options="datePick">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="文档类型">
                    <el-select v-model="query.docType" placeholder="请选择">
                        <el-option label="所有类型" value="all"></el-option>
                        <el-option label="pdf" value="pdf"></el-option>
                        <el-option label="word文档" value="word"></el-option>
                        <el-option label="ppt文档" value="powerpoint"></el-option>
                        <el-option label="excel文档" value="excel"></el-option>
                        <el-option label="网页" value="html"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="文档大小">
                    <!--//0表示不限，1表示0Kb-128kb,2表示128kb-512kb,-->
                    <!--//3表示512kb-1mb,4表示1mb-5mb,5表示5mb以上-->
                    <el-select v-model="query.docSizeLevel" placeholder="请选择">
                        <el-option label="不限大小" :value="0"></el-option>
                        <el-option label="0kb-128kb" :value="1"></el-option>
                        <el-option label="128kb-512kb" :value="2"></el-option>
                        <el-option label="512kb-1mb" :value="3"></el-option>
                        <el-option label="1mb-5mb" :value="4"></el-option>
                        <el-option label="5mb以上" :value="5"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="搜索位置">
                    <!--//0表示不限，1表示0Kb-128kb,2表示128kb-512kb,-->
                    <!--//3表示512kb-1mb,4表示1mb-5mb,5表示5mb以上-->
                    <el-select v-model="query.wordsLocation" placeholder="请选择">
                        <el-option label="所有位置" :value="0"></el-option>
                        <el-option label="文档内容" :value="1"></el-option>
                        <el-option label="文档标题" :value="2"></el-option>

                    </el-select>
                </el-form-item>
                <el-button @click="resetQuery">重置搜索条件</el-button>
            </el-form>
            <el-button type="primary" slot="reference"  >高级搜索</el-button>
        </el-popover>

    </el-form>
        <h4 v-if="searchNothing">
            暂未搜索到任何内容，请重新选择搜索条件
        </h4>
    <el-row>
        <el-col :span="20" :offset="2" style="margin-bottom: 20px;" v-for="(doc, index) in docList" :key="doc.docId">
            <el-card >
                <h4 v-html="doc.docTitle"></h4>
                <p style="font-size: 14px;" v-html="doc.docContent"></p>
                <div style="font-size: 12px;margin-top: 10px;">
                    <span style="margin-top: 20px;display: inline-block">文档类型:{{doc.docType}}</span>
                    <span style="margin-top: 20px;display: inline-block">文档大小:{{doc.docSize}}</span>
                    <div class="bottom clearfix">
                        <span style="float: left;margin-top: 10px;" >文档上传时间:{{doc.docCreateDate}}</span>
                        <el-button type="text" @click="downloadDoc(doc)" size="middle" style="float: right;margin-left: 20px;"  class="button">下载原文档</el-button>
                        <el-button v-if="doc.docConvert===1" @click="previewPdf(doc)" type="text" size="middle" style="float: right" class="button">预览</el-button>
                        <el-button  v-if="doc.docConvert===0" :disabled="true" type="text" size="middle" style="float: right" class="button">文档转换中</el-button>
                        <el-button  v-if="doc.docConvert===2" :disabled="true" type="text" size="middle" style="float: right" class="button">无法预览</el-button>
                    </div>
                </div>
            </el-card>
        </el-col>
    </el-row>
        <div v-show="showLoadMore" style="width: 100%;text-align: center;margin-top: 10px;">
            <el-button-group>
                <el-button v-show="showPrePage" @click="prePage" type="primary" icon="el-icon-arrow-left">上一页</el-button>
                <el-button v-show="showNextPage" @click="nextPage" type="primary">下一页<i class="el-icon-arrow-right el-icon--right"></i></el-button>
            </el-button-group>
        </div>
        <el-button size="big" @click="top" style="z-index: 9999; position: fixed; right: 100px; bottom: 100px;" icon="el-icon-search" circle></el-button>
    </div>
</template>
<script>
    export default {
        name: 'search',
        data(){
            return{
                searchLoading:false,
                searchNothing:false,
                datePick: {
                    shortcuts: [{
                        text: '最近一周',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近三个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一年',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 365);
                            picker.$emit('pick', [start, end]);
                        }
                    }]
                },
                showLoadMore:false,
                showPrePage:false,
                showNextPage:false,
                query:{
                    words:'',
                    docSizeLevel:0,
                    docType:'all',
                    wordsLocation:0,
                    dateRange:null
                },
                docList:[],
                page:1,
                size:10
            }
        },
        created(){

        },
        methods:{
            simpleQuery:function(){
                this.page = 1;
                this.search();
            },
            search:function () {
                var self = this;
                var params={};
                var paramsJson = {};
                if(self.query.words === ''){
                    return;
                }
                paramsJson.words = self.query.words;
                if(self.query.dateRange !=null && self.query.dateRange.length==2){
                    paramsJson.dateStart = self.query.dateRange[0].getTime();
                    paramsJson.dateEnd = self.query.dateRange[1].getTime();
                }
                paramsJson.docSizeLevel = self.query.docSizeLevel;
                paramsJson.wordsLocation = self.query.wordsLocation;
                paramsJson.docType = self.query.docType;
                params.paramsJson = JSON.stringify(paramsJson);
                params.page = self.page;
                params.size = self.size;
                self.searchLoading = true;
                self.$jq.post("/doc-search/doc/searchDoc",params,function (result) {
                    self.searchLoading = false;
                    console.log(result);
                    if(result.code === 1){
                        var list = result.data.list;
                        if(list.length ===0 ){
                            self.searchNothing = true;
                        }else {
                            self.searchNothing = false;
                        }
                        if(list.length === 10){
                            self.showNextPage = true;
                        }else {
                            self.showNextPage = false;
                        }
                        if(params.page > 1){
                            self.showPrePage = true;
                        }else {
                            self.showPrePage = false;
                        }
                        if(self.showPrePage||self.showNextPage){
                            self.showLoadMore = true;
                        }
                        list.forEach(function (doc,index) {
                            doc.docSize = self.$utils.getSize(doc.docSize);
                        })
                        self.docList = list;

                    }else {
                        self.$message.error(result.message);
                    }

                },'json')
            },
            resetQuery:function () {
                var self  = this;
                self.query = {
                    words:'',
                    docSizeLevel:0,
                    docType:'all',
                    wordsLocation:0,
                    dateRange:null
                };
            },
            prePage:function () {
                this.page = this.page-1;
                this.search();
                this.$jq("#content").scrollTop(
                    0
                );
            },
            nextPage:function () {
                this.page = this.page+1;
                this.search();
                this.$jq("#content").scrollTop(
                    0
                );
            },
            top:function () {
                this.$jq("#content").scrollTop(
                    0
                );
            },
            previewPdf:function (doc) {
                var self = this;
                var params={};
                params.docId = doc.docId;
                if(doc.docType == 'evernote'){
                    self.$jq.ajax({
                        url: "/doc-search/doc/everNoteContent",
                        type: 'POST',
                        cache: false,
                        async: false,
                        data: params,
                        dataType:"json",
                        success:function (result,status) {
                            if(result.code===1){
                                const previewDialog= open("预览", "预览","status=no,menubar=yes,toolbar=no");
                                previewDialog.document.open();
                                previewDialog.document.write(result.data.docContent);
                                previewDialog.document.close();
                            }else {
                                self.$message.error(result.message);
                            }
                        },
                        fail: function (err, status) {
                            self.$message.error("网络错误");
                        }
                    })

                }else {
                    var pdfUrl = "/doc-search/doc/pdfFile?docId="+doc.docId;
                    pdfUrl = encodeURIComponent(pdfUrl);
                    window.open("static/pdf/viewer.html?pdfUrl="+pdfUrl);
                }
            },
            downloadDoc:function (doc) {
                window.location = "/doc-search/doc/downloadDocFile?docId="+doc.docId;
            }
        }
    }
</script>
<style>
    .clearfix:before,
    .clearfix:after {
        display: table;
        content: "";
    }

    .clearfix:after {
        clear: both
    }
    em{
        color: red;
        font-style: normal;
        font-weight: bold;
    }
</style>

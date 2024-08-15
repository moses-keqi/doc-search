<template>

    <div>
        <!--<pdf v-bind:src="pdfFile"-->
             <!--@num-pages="pageCount = $event"-->
             <!--@page-loaded="currentPage = $event"-->
        <!--&gt;-->

        <!--</pdf>-->
        <pdf
            v-for="i in numPages"
            :key="i"
            :src="src"
            :page="i"
            style="display: inline-block; width: 100%"
        ></pdf>
    </div>
</template>

<script>
    import pdf from 'vue-pdf'
    export default {
        name: "pdfPreview",
        beforeRouteEnter(to, from, next) {
            next(vm => {
                // 通过 `vm` 访问组件实例
                vm.loadPdf();
            });
        },
        components: {
            pdf
        },
        data(){
            return{
                pdfFile:'',
                numPages: undefined,
                currentPage: 0,
                pageCount: 0,
                src: undefined
            }
        },
        methods:{
            loadPdf(){
                var pdfPath = this.$route.query.pdfPath;
                if (pdfPath) {
                    var loadingTask = pdf.createLoadingTask('https://cdn.mozilla.net/pdfjs/tracemonkey.pdf');
                    this.src = loadingTask;
                    this.src.then(pdf => {
                        this.numPages = pdf.numPages;
                    });
                    this.pdfFile = pdfPath;
                }
            }
        }
    }
</script>

<style scoped>

</style>

const plantumlEncoder = require('plantuml-encoder')
const fs = require('fs')

/**
 * Convert an (Opal) Hash to JSON.
 * @private
 */
const fromHash = function (hash) {
  const object = {}
  const data = hash.$$smap
  for (let key in data) {
    object[key] = data[key]
  }
  return object
}

function UnsupportedFormat (message) {
  this.name = 'UnsupportedFormat'
  this.message = message
  this.stack = (new Error()).stack
}
// eslint-disable-next-line new-parens
UnsupportedFormat.prototype = new Error

function UndefinedPlantumlServer (message) {
  this.name = 'UndefinedPlantumlServer'
  this.message = message
  this.stack = (new Error()).stack
}
// eslint-disable-next-line new-parens
UndefinedPlantumlServer.prototype = new Error

function createImageSrc (doc, text, target, format, vfs) {
  const serverUrl = doc.getAttribute('plantuml-server-url')
  const shouldFetch = doc.isAttribute('plantuml-fetch-diagram')
  let diagramUrl = `${serverUrl}/${format}/${plantumlEncoder.encode(text)}`
  if (shouldFetch) {
    diagramUrl = require('./fetch').save(diagramUrl, doc, target, format, vfs)
  }
  return diagramUrl
}

function processPlantuml (processor, parent, attrs, diagramType, diagramText, context) {
  const doc = parent.getDocument()
  // If "subs" attribute is specified, substitute accordingly.
  // Be careful not to specify "specialcharacters" or your diagram code won't be valid anymore!
  const subs = attrs.subs
  if (subs) {
    diagramText = parent.$apply_subs(diagramText, parent.$resolve_subs(subs), true)
  }

  const plantumlConfigFile = doc.getAttribute('plantuml-config-file')
  if (plantumlConfigFile && fs.existsSync(plantumlConfigFile)) {
    let plantumlConfig = fs.readFileSync(plantumlConfigFile)
    if (plantumlConfig) {
      diagramText = plantumlConfig + '\n' + diagramText
    }
  }

  if (!/^@start([a-z]+)\n[\s\S]*\n@end\1$/.test(diagramText)) {
    if (diagramType === 'plantuml') {
      diagramText = '@startuml\n' + diagramText + '\n@enduml'
    } else if (diagramType === 'ditaa') {
      diagramText = '@startditaa\n' + diagramText + '\n@endditaa'
    } else if (diagramType === 'graphviz') {
      diagramText = '@startdot\n' + diagramText + '\n@enddot'
    }
  }
  const serverUrl = doc.getAttribute('plantuml-server-url')
  const role = attrs.role
  const blockId = attrs.id
  const title = attrs.title
  if (serverUrl) {
    const target = attrs.target
    const format = attrs.format || 'png'
    if (format === 'png' || format === 'svg') {
      const imageUrl = createImageSrc(doc, diagramText, target, format, context.vfs)
      const blockAttrs = {
        role: role ? `${role} plantuml` : 'plantuml',
        target: imageUrl,
        alt: target || 'diagram',
        title
      }
      if (blockId) blockAttrs.id = blockId
      return processor.createImageBlock(parent, blockAttrs)
    } else {
      throw new UnsupportedFormat(`Format '${format}' is unsupported. Only 'png' and 'svg' are supported by the PlantUML server`)
    }
  } else {
    throw new UndefinedPlantumlServer('PlantUML server URL is undefined. Please use the :plantuml-server-url: attribute to configure it.')
  }
}

function plantumlBlock (context) {
  return function () {
    const self = this
    self.onContext(['listing', 'literal'])
    self.positionalAttributes(['target', 'format'])

    self.process((parent, reader, attrs) => {
      if (typeof attrs === 'object' && '$$smap' in attrs) {
        attrs = fromHash(attrs)
      }
      const diagramType = this.name.toString()
      const role = attrs.role
      let diagramText = reader.getString()
      try {
        return processPlantuml(this, parent, attrs, diagramType, diagramText, context)
      } catch (e) {
        if (e.name === 'UnsupportedFormat' || e.name === 'UndefinedPlantumlServer') {
          console.warn(`Skipping ${diagramType} block. ${e.message}`)
          attrs.role = role ? `${role} plantuml-error` : 'plantuml-error'
          return this.createBlock(parent, attrs['cloaked-context'], diagramText, attrs)
        }
        throw e
      }
    })
  }
}

function plantumlBlockMacro (name, context) {
  return function () {
    const self = this
    self.named(name)

    self.process((parent, target, attrs) => {
      if (typeof attrs === 'object' && '$$smap' in attrs) {
        attrs = fromHash(attrs)
      }
      let vfs = context.vfs
      if (typeof vfs === 'undefined' || typeof vfs.read !== 'function') {
        vfs = require('./node-fs')
      }
      const role = attrs.role
      const diagramType = name
      target = parent.$apply_subs(target)
      let diagramText = vfs.read(target)
      try {
        return processPlantuml(this, parent, attrs, diagramType, diagramText, context)
      } catch (e) {
        if (e.name === 'UnsupportedFormat' || e.name === 'UndefinedPlantumlServer') {
          console.warn(`Skipping ${diagramType} block macro. ${e.message}`)
          attrs.role = role ? `${role} plantuml-error` : 'plantuml-error'
          return this.createBlock(parent, 'paragraph', `${e.message} - ${diagramType}::${target}[]`, attrs)
        }
        throw e
      }
    })
  }
}

const antoraAdapter = (file, contentCatalog) => ({
  add: (image) => {
    const { component, version, module } = file.src
    if (!contentCatalog.getById({ component, version, module, family: 'image', relative: image.basename })) {
      contentCatalog.addFile({
        contents: image.contents,
        src: {
          component,
          version,
          module,
          family: 'image',
          mediaType: image.mediaType,
          basename: image.basename,
          relative: image.basename
        }
      })
    }
  }
})

module.exports.register = function register (registry, context = {}) {
  // patch context in case of Antora
  if (typeof context.contentCatalog !== 'undefined' && typeof context.contentCatalog.addFile !== 'undefined' && typeof context.contentCatalog.addFile === 'function' && typeof context.file !== 'undefined') {
    context.vfs = antoraAdapter(context.file, context.contentCatalog)
  }

  if (typeof registry.register === 'function') {
    registry.register(function () {
      this.block('plantuml', plantumlBlock(context))
      this.block('ditaa', plantumlBlock(context))
      this.block('graphviz', plantumlBlock(context))
      this.blockMacro(plantumlBlockMacro('plantuml', context))
      this.blockMacro(plantumlBlockMacro('ditaa', context))
      this.blockMacro(plantumlBlockMacro('graphviz', context))
    })
  } else if (typeof registry.block === 'function') {
    registry.block('plantuml', plantumlBlock(context))
    registry.block('ditaa', plantumlBlock(context))
    registry.block('graphviz', plantumlBlock(context))
    registry.blockMacro(plantumlBlockMacro('plantuml', context))
    registry.blockMacro(plantumlBlockMacro('ditaa', context))
    registry.blockMacro(plantumlBlockMacro('graphviz', context))
  }
  return registry
}
